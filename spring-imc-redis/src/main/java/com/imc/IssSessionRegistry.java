//package com.imc;
//
//import org.springframework.security.core.session.SessionRegistryImpl;
//import org.springframework.session.FindByIndexNameSessionRepository;
//import org.springframework.session.security.SpringSessionBackedSessionRegistry;
//
//public class IssSessionRegistry extends SpringSessionBackedSessionRegistry {
//
//    public IssSessionRegistry(FindByIndexNameSessionRepository sessionRepository) {
//        super(sessionRepository);
//    }
//
//    @Override
//    public void registerNewSession(String sessionId, Object principal) {
//        super.registerNewSession(sessionId, principal);
//        System.out.println(">>>>>>>>>>>>>>>>>>>>> sessionId = " + sessionId);
//
//        System.out.println(">>>>>>>>>>>>>>>>>>>>> principal = " + principal);
//    }
//}
