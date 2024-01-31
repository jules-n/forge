package year.exp.forge.services;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import year.exp.forge.domain.*;
import year.exp.forge.dto.Application;
import year.exp.forge.dto.InitiativeCreate;
import year.exp.forge.dto.Member;
import year.exp.forge.persistence.InitiativeRepository;
import year.exp.forge.persistence.InvitationRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class InitiativeService {

    @Setter(onMethod_={@Autowired})
    InitiativeRepository initiativeRepository;

    @Setter(onMethod_={@Autowired})
    UserService userService;

    @Setter(onMethod_={@Autowired})
    InvitationRepository invitationRepository;


    public void create(String userId, InitiativeCreate dto) {
        Initiative initiative = new Initiative();

        initiative.setName(dto.getName());
        initiative.setDescription(dto.getDescription());
        initiative.setPrivateLink(dto.getPrivateLink());
        initiative.setStatus(Status.JUST_CREATED);
        initiative.setMembers(List.of(userId));

        initiativeRepository.save(initiative);;

    }

    public Page<Initiative> list(PageRequest of, String owner, String userId) {
        if (owner.equals("all")) {
            return initiativeRepository.findByMembersNotContainingAndStatusInSearchOfMembers(userId, of);
        } else {
            return initiativeRepository.findByMembersContaining(userId, of);
        }
    }

    public List<Initiative> list(String userId) {
        return initiativeRepository.findByMembersContainingAndStatusInSearchOfMembers(userId);
    }

    public void apply(String initiativeId, String userId) {
        createInvite(Membership.APPLIED, userId, initiativeId);
    }

    public void invite(String initiativeId, String userId) {
        createInvite(Membership.INVITED, userId, initiativeId);
    }

    private void createInvite(Membership membership, String userId, String initiativeId) {
        Invitation invitation = new Invitation();
        invitation.setMembership(membership);
        invitation.setInitiativeId(initiativeId);
        invitation.setUserId(userId);
        invitationRepository.save(invitation);
    }

    public boolean isMember(String userId, String initiativeId) {
        return initiativeRepository.isMember(userId, initiativeId);
    }

    public year.exp.forge.dto.Initiative get(String initiativeId) {
        Initiative initiative = initiativeRepository.get(initiativeId);
        year.exp.forge.dto.Initiative dto = new year.exp.forge.dto.Initiative();
        dto.setId(initiative.getId());
        dto.setStatus(initiative.getStatus());
        dto.setDescription(initiative.getDescription());
        dto.setName(initiative.getName());
        dto.setPrivateLink(initiative.getPrivateLink());
        List<Member> members = new ArrayList<>();
        initiative.getMembers().stream().forEach(
                x -> {
                    User user = userService.getUser(x);
                    members.add(new Member(user.getName(), user.getSurname(), user.getFatherName(), user.getEmail()));
                }
        );
        dto.setMembers(members);
        return dto;
    }

    public Status updateStatus(String initiativeId, Status status) {
        Initiative initiative = initiativeRepository.get(initiativeId);
        initiative.setStatus(status);
        initiative = initiativeRepository.save(initiative);
        return initiative.getStatus();
    }

    public Page<Application> applicationList(String initiativeId, PageRequest of) {
        Page<Invitation> page = invitationRepository.findByInitiativeIdAndMembership(initiativeId, Membership.APPLIED, of);
        return page.map(x -> {
            Application application = new Application();
            application.setInvitationId(x.getId());
            User user = userService.getUser(x.getUserId());
            Member member = new Member();
            member.setEmail(user.getEmail());
            member.setName(user.getName());
            member.setFatherName(user.getFatherName());
            member.setSurname(user.getSurname());
            application.setMember(member);
            return application;
        });
    }

    public void setDecision(String initiativeId, String applicationId, boolean decision) {
        if (decision) {
            Invitation invitation = invitationRepository.findById(applicationId).get();
            initiativeRepository.addMemberToInitiative(initiativeId, invitation.getUserId());
        }
        invitationRepository.deleteById(applicationId);
    }

    public Page<year.exp.forge.dto.Invitation> invitations(HttpServletRequest request, PageRequest of) {
        year.exp.forge.domain.User user = userService.getUser(request);
        Page<year.exp.forge.domain.Invitation> invitations =
                invitationRepository.findByUserIdAndMembership(user.getId(), Membership.INVITED, of);
        return invitations.map(this::convert);
    }

    private year.exp.forge.dto.Invitation convert (year.exp.forge.domain.Invitation invitation) {
        year.exp.forge.dto.Invitation dto = new year.exp.forge.dto.Invitation();
        dto.setInitiativeId(invitation.getInitiativeId());
        dto.setId(invitation.getId());
        dto.setInitiativeName(get(invitation.getInitiativeId()).getName());
        return dto;
    }

    public void setDecision(HttpServletRequest request, String invitationId, boolean decision) {
        year.exp.forge.domain.Invitation invitation = invitationRepository.findById(invitationId).get();
        var user = userService.getUser(request);
        if (!invitation.getUserId().equals(user.getId())) {
            throw new RuntimeException();
        }
        setDecision(invitation.getInitiativeId(), invitationId, decision);
    }
}
