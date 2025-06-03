package concurrency.exercise8;

import java.util.Arrays;
import java.util.List;

class UserSession {
  public final String userId;
  public final long loginTime;
  public final List<String> permissions;

  public UserSession(String userId, List<String> permissions) {
    this.userId = userId;
    this.loginTime = System.currentTimeMillis();
    this.permissions = permissions;
  }
}

class SessionPublication {
  public static UserSession currentSession;

  public static void publishSession() {
    List<String> perms = Arrays.asList("READ");

    UserSession session = new UserSession("sales789", perms);
    currentSession = session;
  }
}

/*
 * Problem is the public static variable 'currentSession' in the class
 * 'SessionPublication'.
 * * This variable is not thread-safe and can lead to a thread reading a
 * partially constructed object.
 * 
 * * Solution:
 * Use a volatile variable for 'currentSession' to ensure visibility across
 * threads.
 * Use synchronization to ensure that the session is fully constructed before
 * being published.
 * 
 */

class SessionPublicationFixed {
  private static volatile UserSession currentSession;

  public static synchronized void publishSession() {
    List<String> perms = Arrays.asList("READ");

    UserSession session = new UserSession("sales789", perms);
    currentSession = session;
  }

  public static UserSession getCurrentSession() {
    return currentSession;
  }
}