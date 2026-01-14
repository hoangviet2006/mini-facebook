//package com.example.mini_facebook.config;
//import com.example.mini_facebook.model.User;
//import com.example.mini_facebook.repository.IUserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//
//@Component
//public class WebSocketAuthInterceptor implements ChannelInterceptor {
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private IUserRepository userRepository;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            String token = accessor.getFirstNativeHeader("Authorization");
//            System.out.println("CONNECT token: " + token);
//
//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7);
//                String username = jwtUtil.getUsernameByToken(token);
//                System.out.println("Username from token: " + username);
//
//                User user = userRepository.findByUsername(username).orElse(null);
//                if (user != null) {
//                    accessor.setUser(
//                            new UsernamePasswordAuthenticationToken(
//                                    user.getUsername(), null, new ArrayList<>()
//                            )
//                    );
//                    System.out.println("Principal set for user: " + user.getUsername());
//                }
//            }
//        }
//        return message;
//    }
//
//}
