package com.projectmanager.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projectmanager.config.Global;
import com.projectmanager.entities.Feedback;
import com.projectmanager.entities.Usuario;
import com.projectmanager.forms.FeedbackForm;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.repositories.FeedbackRepository;

@Service("feedbackService")
public class FeedbackServiceImpl implements FeedbackService{

    @Autowired
    FeedbackRepository feedbackRepository;
    @Autowired
    private GitService gitService; // Injete o serviço que obtém os repositórios do GitHub

    @Override
    public Iterable<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback find(int id) {
        return feedbackRepository.findById(id).get();
    }

    @Override
    public Feedback save(String repoName, String accessToken, FeedbackForm feedbackForm) throws IOException{
        Usuario user = gitService.getUsuario(accessToken);
        RepositoryModel repo = gitService.getRepository(accessToken, repoName);
        Long colaboratorId = gitService.getCollaboratorId(accessToken,feedbackForm.getCollaborators().get(0),repo);
        Feedback feedback = new Feedback();
        feedback.setComentario(feedbackForm.getMensagem());
        feedback.setDestinatario(colaboratorId.intValue());
        feedback.setEscritor(user.getUsername());
        feedback.setProjeto(Math.toIntExact(repo.getId()));

        return feedbackRepository.save(feedback);
    }

    @Override
    public void delete(int id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public Collection<Feedback> getFeedbacksUsuarioProjeto(String accessToken, String repoName) throws IOException{
        ArrayList<Feedback> feedbacks = new ArrayList<>();
        Usuario user = gitService.getUsuario(accessToken);
        int userId = (int) user.getId();
        RepositoryModel repo = gitService.getRepository( accessToken,  repoName);

        for (Feedback feedback : findAll()) {
            if (feedback.getDestinatario() == userId && feedback.getProjeto() == (int) repo.getId()){
                System.out.println("Achei um feedback");
                feedbacks.add(feedback);
            }
        }

        return feedbacks;
    }

}
