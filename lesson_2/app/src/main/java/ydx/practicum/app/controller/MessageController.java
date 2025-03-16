package ydx.practicum.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ydx.practicum.app.service.BlacklistWordService;
import ydx.practicum.app.service.BlockUserService;
import ydx.practicum.app.service.MessageService;

@RestController
public class MessageController {

    private final MessageService messageService;
    private final BlockUserService blockUserService;
    private final BlacklistWordService blacklistWordService;

    public MessageController(MessageService messageService, BlockUserService blockUserService, BlacklistWordService blacklistWordService) {
        this.messageService = messageService;
        this.blockUserService = blockUserService;
        this.blacklistWordService = blacklistWordService;
    }

    // для упрощения тестирования апи выполнено в форме GET запроса
    @GetMapping("/send")
    public String sendMessage(
            @RequestParam String userTo,
            @RequestParam String userFrom,
            @RequestParam String message) {
        messageService.sendMessage(userTo, userFrom, message);
        return "Сообщение отправлено!";
    }

    @GetMapping("/block-user")
    public String blockUser(
            @RequestParam String forUser,
            @RequestParam String userToBlock
    ) {
        blockUserService.blockUser(forUser, userToBlock);
        return "Пользователь %s добавлен в блэклист пользователя %s".formatted(userToBlock, forUser);
    }

    @GetMapping("/block-word")
    public String blockWord(
            @RequestParam String word
    ) {
        blacklistWordService.blockWord(word);
        return "Слово '%s' теперь запрещено".formatted(word);
    }
}
