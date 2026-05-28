package net.chess_platform.user_service.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.user_service.dto.AvatarDto;
import net.chess_platform.user_service.exception.EntityNotFoundException;
import net.chess_platform.user_service.exception.InvalidImageException;
import net.chess_platform.user_service.exception.InvalidUserException;
import net.chess_platform.user_service.model.User;

@Service
public class AvatarService {

    private static final String IMAGE_FOLDER = "/avatars";

    public static final UUID DEFAULT_AVATAR = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final UserService userService;

    public AvatarService(UserService userService) {
        this.userService = userService;
    }

    public AvatarDto upload(MultipartFile file, CurrentUser currentUser) {
        if (file.isEmpty()) {
            throw new InvalidImageException("Image is empty");
        }

        if (file.getSize() > 2_097_152L) {
            throw new InvalidImageException("Image size must be less than 2MB");
        }

        var id = UUID.randomUUID();

        var u = userService.findById(currentUser.id());
        if (u == null) {
            throw new InvalidUserException();
        }

        try (
                var inputStream = new BufferedInputStream(file.getInputStream());
                var outputStream = new BufferedOutputStream(
                        Files.newOutputStream(Paths.get(IMAGE_FOLDER + "/" + id)))) {

            var format = detectImageFormat(inputStream);
            if (!format.equals("jpeg") && !format.equals("png")) {
                throw new InvalidImageException("Only JPEG and PNG images are supported");
            }

            var bytes = new byte[2_097_152];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, bytesRead);
            }

            outputStream.flush();

            try {
                var a = u.avatar();
                if (a != null && !a.equals(DEFAULT_AVATAR.toString())) {
                    Files.delete(Path.of(IMAGE_FOLDER + '/' + a));
                }
            } catch (NoSuchFileException e) {
            }

            var update = new User.Update();
            update.setId(currentUser.id());
            update.setAvatar(id.toString());

            userService.update(update, currentUser);

            return new AvatarDto(id);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String detectImageFormat(InputStream fileInputStream) {
        fileInputStream.mark(Integer.MAX_VALUE);
        try (var inputStream = ImageIO.createImageInputStream(fileInputStream)) {
            var imageReaders = ImageIO.getImageReaders(inputStream);
            if (!imageReaders.hasNext()) {
                return null;
            }

            fileInputStream.reset();
            return imageReaders.next().getFormatName().toLowerCase();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource download(UUID id) {
        var path = Paths.get(IMAGE_FOLDER + '/' + id);
        var resource = new FileSystemResource(path);

        if (!resource.exists()) {
            throw new EntityNotFoundException();
        }

        return resource;
    }

    public AvatarDto delete(CurrentUser currentUser) {
        try {
            var u = userService.findById(currentUser.id());
            if (u == null) {
                throw new InvalidUserException();
            }

            var a = u.avatar();
            if (a == null || a.equals(DEFAULT_AVATAR.toString())) {
                return new AvatarDto(DEFAULT_AVATAR);
            }

            Files.delete(Paths.get(IMAGE_FOLDER + '/' + a));

            var update = new User.Update();
            update.setId(currentUser.id());
            update.setAvatar(DEFAULT_AVATAR.toString());

            userService.update(update, currentUser);

            return new AvatarDto(DEFAULT_AVATAR);

        } catch (NoSuchFileException e) {
            return new AvatarDto(DEFAULT_AVATAR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
