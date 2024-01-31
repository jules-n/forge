package year.exp.forge.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.CreateThreadAndRunRequest;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.ThreadRequest;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import year.exp.forge.domain.Role;
import year.exp.forge.dto.*;
import year.exp.forge.persistence.UserRepository;
import year.exp.forge.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Setter(onMethod_ = {@Autowired})
    JWTService jwtService;

    @Setter(onMethod_ = {@Autowired})
    UserRepository userRepository;

    @Setter(onMethod_ = {@Autowired})
    DiiaService diiaService;

    @Setter(onMethod_ = {@Autowired})
    GcpStorageService gcpStorageService;

    @Setter(onMethod_ = {@Autowired})
    private ModelMapper modelMapper;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.assistants.search}")
    private String searchAssist;


    @Value("${openai.assistants.file}")
    private String fileAssist;

    private static final String OPENAI_FILE_URL = "https://api.openai.com/v1/files";

    public year.exp.forge.domain.User getUser(HttpServletRequest request) {
        String email = jwtService.getEmail(request);
        return userRepository.getByCriteria(Map.of("email", email));
    }

    public year.exp.forge.domain.User getUser(String id) {
        return userRepository.getByCriteria(Map.of("id", id));
    }

    public year.exp.forge.domain.User getUserByEmail(String email) {
        return userRepository.getByCriteria(Map.of("email", email));
    }

    public Boolean isRegistered(HttpServletRequest request) {
        return getUser(request) != null;
    }

    public Role getRole(HttpServletRequest request) {
        return getUser(request).getRole();
    }

    public Boolean register(HttpServletRequest request, User dto) {
        String email = jwtService.getEmail(request);

        if (userRepository.getByCriteria(Map.of("email", email)) != null) {
            throw new RuntimeException("Email duplicate");
        }

        if (diiaService.checkName(dto)) {
            year.exp.forge.domain.User user = new year.exp.forge.domain.User();
            user.setEmail(email);
            user.setName(dto.getName());
            user.setRole(Role.USER);
            user.setSurname(dto.getSurname());
            user.setFatherName(dto.getFatherName());
            userRepository.save(user);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void addModerator(String email) {
        userRepository.updateByCriteria(Map.of("email", email), Map.of("role", Role.MODERATOR));
    }

    @SneakyThrows
    public URL uploadPhoto(HttpServletRequest request, MultipartFile file) {
        year.exp.forge.domain.User user = getUser(request);
        String url = gcpStorageService.upload(file, "photos");
        user.setPhotoLink(url);
        userRepository.save(user);
        return gcpStorageService.signFile(url);
    }

    public void update(HttpServletRequest request, Social social) {
        final year.exp.forge.domain.User user = getUser(request);
        year.exp.forge.domain.User update = user;
        if (StringUtil.notEmpty(social.getAbout())) {
            update.setAbout(social.getAbout());
        }
        if (StringUtil.notEmpty(social.getPosition())) {
            update.setPosition(social.getPosition());
        }
        if (social.getContacts() != null && !social.getContacts().isEmpty()) {
            Map<String, String> contacts = user.getContacts();
            contacts.putAll(social.getContacts());
            update.setContacts(contacts);
        }
        userRepository.save(update);
    }

    public void uploadFile(HttpServletRequest request, MultipartFile file) throws IOException, InterruptedException {
        String fileId = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(OPENAI_FILE_URL);

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", file.getInputStream(), ContentType.DEFAULT_BINARY, file.getOriginalFilename())
                    .addTextBody("purpose", "assistants")
                    .build();

            httpPost.setEntity(entity);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            String json = null;
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                json = EntityUtils.toString(response.getEntity());
            }
            if (json == null) throw new RuntimeException("failed to upload file to open ai");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(json, Map.class);
            fileId = map.get("id").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OpenAiService service = new OpenAiService(apiKey);
        CreateThreadAndRunRequest createThreadAndRunRequest = new CreateThreadAndRunRequest();
        createThreadAndRunRequest.setAssistantId(fileAssist);
        ThreadRequest threadRequest = new ThreadRequest();
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setRole("user");
        messageRequest.setContent("analyze and retrieve keywords from file with id " + fileId);
        messageRequest.setFileIds(List.of(fileId));
        threadRequest.setMessages(List.of(messageRequest));
        createThreadAndRunRequest.setThread(threadRequest);
        Run run = service.createThreadAndRun(createThreadAndRunRequest);
        while (!run.getStatus().equals("completed")) {
            Thread.sleep(600l);
            run = service.retrieveRun(run.getThreadId(), run.getId());
            if (run.getStatus().equals("failed") || run.getStatus().equals("cancelled") || run.getStatus().equals("expired")) {
                break;
            }
        }

        OpenAiResponse<Message> openAiResponse = service.listMessages(run.getThreadId());
        String keyWords = openAiResponse.getData().get(0).getContent().get(0).getText().getValue();

        year.exp.forge.domain.User user = getUser(request);
        user.setKeyWords(keyWords);
        userRepository.save(user);
    }

    public Page<Registered> list(PageRequest of) {
        Page<year.exp.forge.domain.User> page = userRepository.findByRoleInUser(of);
        return page.map(x -> modelMapper.map(x, Registered.class));
    }

    @SneakyThrows
    public List<Registered> search(String request) {
        var users = userRepository.findByRoleInUser();
        var candidates = users.stream().map(this::convert).toList();
        Gson gson = new Gson();
        String userData = gson.toJson(candidates);
        OpenAiService service = new OpenAiService(apiKey);
        CreateThreadAndRunRequest createThreadAndRunRequest = new CreateThreadAndRunRequest();
        createThreadAndRunRequest.setAssistantId(searchAssist);
        ThreadRequest threadRequest = new ThreadRequest();
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setRole("user");
        messageRequest.setContent("request: " + request + " user data: " + userData);
        threadRequest.setMessages(List.of(messageRequest));
        createThreadAndRunRequest.setThread(threadRequest);
        Run run = service.createThreadAndRun(createThreadAndRunRequest);
        while (!run.getStatus().equals("completed")) {
            Thread.sleep(600l);
            run = service.retrieveRun(run.getThreadId(), run.getId());
            if (run.getStatus().equals("failed") || run.getStatus().equals("cancelled") || run.getStatus().equals("expired")) {
                break;
            }
        }

        OpenAiResponse<Message> openAiResponse = service.listMessages(run.getThreadId());
        String array = openAiResponse.getData().get(0).getContent().get(0).getText().getValue();
        List<String> ids = gson.fromJson(array, List.class);
        return ids.stream().map(
                id -> {
                    year.exp.forge.domain.User user = getUser(id);
                    return modelMapper.map(user, Registered.class);
                }
        ).toList();
    }

    private Candidate convert(year.exp.forge.domain.User user) {
        return new Candidate(user.getId(), user.getEmail(),
                user.getAbout(), user.getPosition(), user.getKeyWords(), user.getEducations());
    }
}
