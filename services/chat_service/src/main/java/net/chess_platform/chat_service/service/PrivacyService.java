package net.chess_platform.chat_service.service;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.dto.PrivacyDto;
import net.chess_platform.chat_service.model.Privacy;
import net.chess_platform.chat_service.repository.PrivacyRepository;
import net.chess_platform.common.security.CurrentUser;

@Service
public class PrivacyService {

    private final PrivacyRepository privacyRepository;

    public PrivacyService(PrivacyRepository privacyRepository) {
        this.privacyRepository = privacyRepository;
    }

    public PrivacyDto findAll(CurrentUser currentUser) {
        var p = privacyRepository.findByUserId(currentUser.id());

        Privacy.Restriction.Setting friends = null;

        for (var r : p.getRestrictions()) {
            switch (r.getResource()) {
                case Privacy.Restriction.Resource.FRIENDS -> friends = r.getSetting();
            }
        }

        return new PrivacyDto(friends);
    }

    public void update(PrivacyDto privacy, CurrentUser currentUser) {
        privacyRepository.update(privacy, currentUser.id());
    }
}
