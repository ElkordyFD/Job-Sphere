# Job Sphere - Discussion Guide

## 📌 Project Overview

**Job Sphere** is a Java Desktop Application that connects job seekers (Applicants) with employers (Companies). The application demonstrates **5 Design Patterns** in a real-world context.

---

## 🎯 Design Patterns Summary

| # | Pattern | Core Class | Purpose |
|---|---------|------------|---------|
| 1 | Singleton | `DataManager` | Single shared data instance |
| 2 | Factory | `UserFactory` | Create different user types |
| 3 | Builder | `JobBuilder` | Build complex Job objects |
| 4 | Strategy | `SearchStrategy` | Flexible search algorithms |
| 5 | State | `ApplicationState` | Manage application lifecycle |
| 6 | Proxy | `LoginProxy` | Control access to login |

---

# Pattern 1: Singleton

## What is it?
Ensures only **ONE instance** of a class exists throughout the application.

## Where in Code?
**File:** `DataManager.java`

```java
public class DataManager {
    private static DataManager instance;

    private DataManager() { }  // Private constructor

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
}
```

## How it's Used?
```java
// In LoginPanel
DataManager.getInstance().login(user, pass);

// In ApplicantPanel
DataManager.getInstance().getJobs();

// In CompanyPanel
DataManager.getInstance().addJob(job);
```

## Why?
All panels share the **same data**. If each panel created its own DataManager, users registered in one panel wouldn't be visible in another!

---

# Pattern 2: Factory

## What is it?
Creates objects without exposing the creation logic to the client.

## Where in Code?
**File:** `UserFactory.java`

```java
public class UserFactory {

    public static User createUser(String type, String username, String password, String email) {
        if (type == null) {
            throw new IllegalArgumentException("User type cannot be null");
        }

        switch (type.toUpperCase()) {
            case "APPLICANT":
                return new Applicant(username, password, email);
            case "COMPANY":
                return new Company(username, password, email);
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }
}
```

## How it's Used?
```java
// In LoginPanel - handleRegister()
String role = (String) roleCombo.getSelectedItem();  // "APPLICANT" or "COMPANY"
User newUser = UserFactory.createUser(role, user, pass, email);
DataManager.getInstance().registerUser(newUser);
```

## Why?
The UI doesn't need to know about `Applicant` or `Company` classes. It just says "create a user of this type" and the factory handles it.

---

# Pattern 3: Builder

## What is it?
Constructs complex objects **step-by-step** with a fluent interface.

## Where in Code?
**File:** `JobBuilder.java`

```java
public class JobBuilder {
    private String title;
    private String description;
    private String requirements;
    private String companyUsername;

    public JobBuilder setTitle(String title) {
        this.title = title;
        return this;  // Return self for chaining
    }

    public JobBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public Job build() {
        return new Job(UUID.randomUUID().toString(), title, description, 
                       companyUsername, requirements);
    }
}
```

## How it's Used?
```java
// In CompanyPanel - Post Job button
Job job = new JobBuilder()
    .setTitle(titleField.getText())
    .setDescription(descArea.getText())
    .setRequirements(reqArea.getText())
    .setCompanyUsername(user.getUsername())
    .build();
```

## Why?
Much cleaner than: `new Job(id, title, desc, company, req)` - especially when you have many fields.

---

# Pattern 4: Strategy

## What is it?
Defines a family of algorithms and makes them **interchangeable** at runtime.

## Where in Code?
**File:** `SearchStrategy.java` (Interface)
**File:** `KeywordSearchStrategy.java` (Implementation)

```java
// Interface
public interface SearchStrategy {
    List<Job> search(List<Job> allJobs, String query);
}

// Implementation
public class KeywordSearchStrategy implements SearchStrategy {
    @Override
    public List<Job> search(List<Job> allJobs, String query) {
        if (query == null || query.isEmpty()) {
            return allJobs;
        }
        return allJobs.stream()
            .filter(job -> job.getTitle().toLowerCase().contains(query.toLowerCase())
                    || job.getDescription().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
    }
}
```

## How it's Used?
```java
// In ApplicantPanel
private SearchStrategy searchStrategy = new KeywordSearchStrategy();

private void refreshJobList() {
    List<Job> allJobs = DataManager.getInstance().getJobs();
    List<Job> filtered = searchStrategy.search(allJobs, searchField.getText());
    // Display filtered jobs in table
}
```

## Why?
Easy to add new search types (by location, by salary) without changing ApplicantPanel code!

---

# Pattern 5: State

## What is it?
Object behavior changes based on its internal **state**. The object appears to change its class.

## Where in Code?
**Files:** `ApplicationState.java`, `AppliedState.java`, `ReviewedState.java`, `AcceptedState.java`, `RejectedState.java`

```java
// Interface
public interface ApplicationState {
    void next(JobApplication application);
    String getStatusName();
}

// Applied State
public class AppliedState implements ApplicationState {
    @Override
    public void next(JobApplication application) {
        application.setState(new ReviewedState());  // Transition!
    }
    
    @Override
    public String getStatusName() {
        return "Applied";
    }
}

// In JobApplication
public void next() {
    state.next(this);  // Delegate to current state
}
```

## State Transitions
```
Applied → Reviewed → Accepted
                  ↘ Rejected
```

## How it's Used?
```java
// In CompanyPanel - moveState()
app.next();  // State pattern handles the transition
refreshApps();  // Table shows new status
```

## Why?
No `if-else` chains to check current status. Each state knows what comes next!

---

# Pattern 6: Proxy

## What is it?
Provides a **surrogate** for another object to control access to it.

## Where in Code?
**Files:** `LoginService.java`, `RealLoginService.java`, `LoginProxy.java`

```java
// Interface
public interface LoginService {
    User login(String username, String password);
}

// Real Service
public class RealLoginService implements LoginService {
    @Override
    public User login(String username, String password) {
        return sessionManager.login(userRepository, username, password);
    }
}

// Proxy - Adds logging
public class LoginProxy implements LoginService {
    private final LoginService realLoginService;

    @Override
    public User login(String username, String password) {
        System.out.println("[LoginProxy] Login attempt for user: " + username);
        return realLoginService.login(username, password);
    }
}
```

## How it's Used?
```java
// In DataManager constructor
RealLoginService realLoginService = new RealLoginService(userRepository, sessionManager);
this.loginService = new LoginProxy(realLoginService);

// When login is called
public User login(String username, String password) {
    return loginService.login(username, password);  // Goes through proxy
}
```

## Why?
- Adds **logging** without modifying the real login service
- Can easily add validation, caching, rate limiting later
- Client (LoginPanel) doesn't know it's using a proxy

---

# 📊 Class Distribution

## Core Package (com.jobsphere.core)

| Pattern | Classes |
|---------|---------|
| Singleton | `DataManager`, `SessionManager` |
| Factory | `UserFactory`, `User`, `Applicant`, `Company` |
| Builder | `JobBuilder`, `Job` |
| Strategy | `SearchStrategy`, `KeywordSearchStrategy` |
| State | `ApplicationState`, `AppliedState`, `ReviewedState`, `AcceptedState`, `RejectedState`, `JobApplication` |
| Proxy | `LoginService`, `RealLoginService`, `LoginProxy` |
| Repository | `UserRepository`, `JobRepository`, `ApplicationRepository` + InMemory implementations |

## UI Package (com.jobsphere.ui)

| Class | Purpose |
|-------|---------|
| `MainFrame` | Main window with CardLayout |
| `LoginPanel` | Login/Register screen |
| `ApplicantPanel` | Job seeker dashboard |
| `CompanyPanel` | Employer dashboard |

---

# 🎬 Demo Flow

## Step 1: Register Users
1. Open app → LoginPanel
2. Enter username: `TechCorp`, password: `123`, Role: `COMPANY`
3. Click **Register** → Shows "Registration Successful"
4. Enter username: `John`, password: `123`, Role: `APPLICANT`
5. Click **Register** → Shows "Registration Successful"

**Patterns Shown:** Factory (creates users), Singleton (stores users)

## Step 2: Post Jobs
1. Login as `TechCorp`
2. Go to "Post New Job" tab
3. Fill: Title: `Java Developer`, Description: `...`, Requirements: `...`
4. Click **Post Job**

**Patterns Shown:** Builder (creates job)

## Step 3: Search & Apply
1. Logout → Login as `John`
2. See all jobs in table
3. Type `Java` in search → Only matching jobs shown
4. Select job → Click **Apply with Resume**

**Patterns Shown:** Strategy (filters jobs)

## Step 4: Manage Applications
1. Logout → Login as `TechCorp`
2. Go to "Manage Applications" tab
3. See application with status: `Applied`
4. Click **Move to Next Stage** → Status: `Reviewed`
5. Click again → Status: `Accepted`

**Patterns Shown:** State (status transitions)

## Step 5: Check Console
1. Check console output for `[LoginProxy]` messages
2. Shows all login attempts were logged

**Patterns Shown:** Proxy (intercepts login)

---

# ✅ Quick Reference

| When you... | Pattern Used | Code Location |
|-------------|--------------|---------------|
| Access any data | Singleton | `DataManager.getInstance()` |
| Register user | Factory | `UserFactory.createUser()` |
| Post job | Builder | `new JobBuilder()...build()` |
| Search jobs | Strategy | `searchStrategy.search()` |
| Move application stage | State | `app.next()` |
| Login | Proxy | `loginService.login()` |

---

# 📝 Key Points for Discussion

1. **Singleton** - One instance, accessed everywhere with `getInstance()`
2. **Factory** - Creates objects based on type, hides creation logic
3. **Builder** - Step-by-step construction, method chaining
4. **Strategy** - Interface + implementations, swappable algorithms
5. **State** - Each state is a class, transitions handled internally
6. **Proxy** - Wraps real object, adds behavior transparently

---

Good luck with your discussion! 🚀
