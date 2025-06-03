package concurrency.exercise8;

class XXX {
  private int a;

  XXX(int a) {
    this.a = a;
  }

  XXX(XXX x) {
    this.a = x.a;
  }

  void setA(int a) {
    this.a = a;
  }
}

class YYY {
  ToBeImmutable tbi;

  void register(ToBeImmutable tbi) {
    this.tbi = tbi;
  }
}

class ToBeImmutableOld {
  private int age;
  private String name;
  private XXX xxx;

  public ToBeImmutableOld(int age, String name, XXX xxx, YYY yyy) {
    this.age = age;
    this.name = name;
    this.xxx = xxx;
    // yyy.register(this);
  }

  public int getAge() {
    return age;
  }

  public String getName() {
    return name;
  }

  public XXX getXXX() {
    return xxx;
  }
}

/**
 * Problems that are making the class mutable:
 * 1. The class has mutable fields (e.g., 'xxx, name, age').
 * 2. The class returns its instance inside construction (e.g., 'yyy.regsiter')
 * what could
 * lead to a situation where the object is not fully constructed when it is
 * registered.
 * 3. The class does not provide a way to create a new instance with the same
 * values, which
 * could lead to unintended modifications of the original instance.
 */

class ToBeImmutable {
  private final int age;
  private final String name;
  private final XXX xxx;

  public ToBeImmutable(int age, String name, XXX xxx, YYY yyy) {
    this.age = age;
    this.name = name;
    this.xxx = new XXX(xxx);
  }

  public static ToBeImmutable createAndRegister(int age, String name, XXX xxx, YYY yyy) {
    ToBeImmutable instance = new ToBeImmutable(age, name, xxx, yyy);
    yyy.register(instance);
    return instance;
  }

  public int getAge() {
    return age;
  }

  public String getName() {
    return name;
  }

  public XXX getXXX() {
    return new XXX(xxx);
  }
}
