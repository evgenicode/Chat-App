package com.packagename.myapp;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;


@Route
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")

public class MainView extends VerticalLayout {

    private final UnicastProcessor<ChatMessage> publisher;
    private final Flux<ChatMessage> messages;
    private String username;

    public MainView(UnicastProcessor<ChatMessage> publisher, Flux<ChatMessage> messages) {
        this.publisher = publisher;
        this.messages = messages;

        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        addClassName("main-view");

        H1 header = new H1("Chat App");
        header.getElement().getThemeList().add("dark");

        add(header);

        askUsername();
    }


    private void askUsername() {
        HorizontalLayout layout = new HorizontalLayout();
        TextField placeholderField = new TextField();
        placeholderField.setPlaceholder("Choose a name");
        Button startButton = new Button("Start chat");
        layout.add(placeholderField, startButton);
        add(layout);
        
        startButton.addClickListener(click -> {
            username = placeholderField.getValue();
            remove(layout);
            showChat();
        });
    }


    private void showChat() {
        MessageList messageList = new MessageList();
        add(messageList, createInputLayout());
        expand(messageList);

        //Allow UI update from an outside thread

        messages.subscribe(message -> {
            getUI().ifPresent(ui -> ui.access(() ->
            messageList.add(new Paragraph(message.getFrom() + ": " + message.getMessage()))
            ));
        });
    }

    private Component createInputLayout() {
        HorizontalLayout layout = new HorizontalLayout();

        TextField messageField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(messageField, sendButton);
        layout.setWidth("100%");
        layout.expand(messageField);

        sendButton.addClickListener(click -> {
            publisher.onNext(new ChatMessage(username, messageField.getValue()));
            messageField.clear();
            messageField.focus();
        });
        messageField.focus();

        return layout;
    }


}


