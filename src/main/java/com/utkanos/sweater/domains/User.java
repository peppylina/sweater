package com.utkanos.sweater.domains;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "usr")
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //Spring security автоматически проверяет и подставляет именно эти параметры: username и password
    @NotBlank(message = "username can not be empty!")
    private String username;
    @NotBlank(message = "password can not be empty")
    private String password;

    @Transient
    private String password2 = "";

    private String ava = null;

    @NotBlank(message = "email can not be empty")
    @Email(message = "Email is incorrect!")
    private String email;
    private String activationCode;

    //этот параметр spring security автоматически берет как флаг, который определяет, активирован ли аккаунт, или нет.
    //Если false, то нам не позволят войти в этот аккаунт.
    private boolean active = false;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages = Collections.emptySet();


    //избавляет нас от головной боли по создании таблицы Enum'ов, fetch - способ подгрузки:
    //lazy - ленивая подгрузка, подгружает поле только при обращении к нему,
    //eager - жадная, сразу подгружает это поле из бд при выгрузки объекта.
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    //эта аннотация говорит, что это поле будет храниться в отдельной таблице, которая будет соединяться
    // (join) с текущей табличкой по полю "user_id"
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    //говорим, что хотим хранить этот enum в виде строки
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    //подписчики пользователя
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = {@JoinColumn(name = "profile_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscriber_id")}
    )
    private Set<User> subscribers = new HashSet<>();

    //подписки пользователя
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = {@JoinColumn(name = "subscriber_id")},
            inverseJoinColumns = {@JoinColumn(name = "profile_id")}
    )
    private Set<User> subscriptions = new HashSet<>();


    //functions

    public ArrayList<Message> getListOfMessages() {
        return messages
                .stream()
                .sorted((mess1, mess2) -> mess2.getTime().compareTo(mess1.getTime()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public int getAmountOfSubscriptions() { return subscriptions.size(); }

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    public int getAmountOfSubscribers() {
        return subscribers.size();
    }

    public ArrayList<User> getListOfSubscribers() {
        return subscribers
                    .stream()
                    .sorted((user1, user2) ->
                        user1.getUsername().compareToIgnoreCase(user2.getUsername()))
                    .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<User> getListOfSubscriptions() {
        return subscriptions
                .stream()
                .sorted((user1, user2) ->
                        user1.getUsername().compareToIgnoreCase(user2.getUsername()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean hasSubscriber(int subId) {
        for (User sub : getListOfSubscribers()) {
            if(sub.getId() == subId) return true;
        }
        return false;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return active == user.active &&
                Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, active, roles);
    }

    //getters and setters

    public String getAva() {
        return ava;
    }

    public void setAva(String ava) {
        this.ava = ava;
    }

    public Set<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<User> subscribers) {
        this.subscribers = subscribers;
    }

    public Set<User> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<User> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
}
