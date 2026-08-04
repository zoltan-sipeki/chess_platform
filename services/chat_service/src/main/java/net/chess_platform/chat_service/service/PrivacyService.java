package net.chess_platform.chat_service.service;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.authorization.PrivacyAuthorizationService;
import net.chess_platform.chat_service.dto.PrivacyDto;
import net.chess_platform.chat_service.dto.UpdatePrivacyDto;
import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.model.Privacy;
import net.chess_platform.chat_service.repository.PrivacyRepository;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.security.CurrentUser;

@Service
public class PrivacyService {

    private final PrivacyRepository privacyRepository;

    private final PrivacyAuthorizationService authService;

    public PrivacyService(PrivacyRepository privacyRepository, PrivacyAuthorizationService authService) {
        this.privacyRepository = privacyRepository;
        this.authService = authService;
    }

    public PrivacyDto findAll(CurrentUser currentUser) {
        var auth = authService.authorizePrivacyRead(currentUser);

        MongoQueryFragment<Privacy> fragment = auth.getQueryFragment(Privacy.class);

        var p = privacyRepository.findOne(fragment.getCriteria());

        if (p == null) {
            return new PrivacyDto(null);
        }

        Privacy.Restriction.Setting friends = null;

        for (var r : p.getRestrictions()) {
            switch (r.getResource()) {
                case Privacy.Restriction.Resource.FRIENDS -> friends = r.getSetting();
            }
        }

        return new PrivacyDto(friends);
    }

    public void update(UpdatePrivacyDto privacy, CurrentUser currentUser) {
        var auth = authService.authorizePrivacyUpdate(currentUser);

        MongoQueryFragment<Privacy> fragment = auth.getQueryFragment(Privacy.class);

        long modifiedCount = privacyRepository.update(fragment.getCriteria(), privacy);

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }
    }
}
