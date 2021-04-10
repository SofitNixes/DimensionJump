package com.spaceman.dimensionJump.fancyMessage;

import com.google.common.base.Strings;
import com.spaceman.dimensionJump.fancyMessage.book.BookPage;
import com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.dimensionJump.fancyMessage.colorTheme.MultiColor;
import com.spaceman.dimensionJump.fancyMessage.events.ClickEvent;
import com.spaceman.dimensionJump.fancyMessage.events.HoverEvent;
import com.spaceman.dimensionJump.fancyMessage.events.ScoreEvent;
import com.spaceman.dimensionJump.fancyMessage.events.TextEvent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class TextComponent implements Cloneable {
    
    public final static String APOSTROPHE = "\"";
    public final static String NEW_LINE = "\n";
    public final static String PLACE_HOLDER = "%s";
    public static String indexedPlaceHolder(int index) { return "%" + index + "$s"; }
    
    private String type = "text";
    private BookPage pageNumber = null;
    private String text;
    private String color;
    private String insertion = null;
    private ArrayList<Attribute> attributes;
    private ArrayList<TextEvent> textEvents;
    public ArrayList<Message> translateWith = new ArrayList<>();
    
    public TextComponent() {
        this("", new MultiColor("#ffffff").getColorAsValue(), new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text) {
        this(text, new MultiColor("#ffffff").getColorAsValue());
    }
    
    public TextComponent(String text, String color) {
        this(text, color, new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, ChatColor color) {
        this(text, color, new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, ColorTheme.ColorType color) {
        this(text, color.name(), new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, Color color) {
        this(text, new MultiColor(color).getColorAsValue(), new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, MultiColor color) {
        this(text, color.getColorAsValue(), new ArrayList<>(), new ArrayList<>());
    }
    
    public TextComponent(String text, String color, List<TextEvent> textEvents, List<Attribute> attribute) {
        this.text = text;
        setColor(color);
        if (textEvents != null) this.textEvents = new ArrayList<>(textEvents);
        else this.textEvents = new ArrayList<>();
        if (textEvents != null) this.attributes = new ArrayList<>(attribute);
        else this.attributes = new ArrayList<>();
    }
    
    public TextComponent(String text, ChatColor color, List<TextEvent> textEvents, List<Attribute> attribute) {
        this(text, color.name().toLowerCase(), textEvents, attribute);
    }
    
    @Override
    public String toString() {
        return getRawText();
    }
    
    public static TextComponent textComponent() {
        return new TextComponent("", new MultiColor("#ffffff").getColorAsValue(), null, null);
    }
    
    public static TextComponent textComponent(String text) {
        return new TextComponent(text, new MultiColor("#ffffff").getColorAsValue());
    }
    
    public static TextComponent textComponent(String text, String color) {
        return new TextComponent(text, color);
    }
    
    public static TextComponent textComponent(String text, ChatColor color) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue());
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type) {
        return new TextComponent(text, type.name());
    }
    
    public static TextComponent textComponent(String text, Color color) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue());
    }
    
    public static TextComponent textComponent(String text, MultiColor color) {
        return new TextComponent(text, color.getColorAsValue());
    }
    
    public static TextComponent textComponent(String text, String color, String insertion) {
        return new TextComponent(text, color).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, ChatColor color, String insertion) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, String insertion) {
        return new TextComponent(text, type.name()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, Color color, String insertion) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, MultiColor color, String insertion) {
        return new TextComponent(text, color.getColorAsValue()).setInsertion(insertion);
    }
    
    public static TextComponent textComponent(String text, String color, TextEvent... textEvents) {
        return new TextComponent(text, color, Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, ChatColor color, TextEvent... textEvents) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, TextEvent... textEvents) {
        return new TextComponent(text, type.name(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, Color color, TextEvent... textEvents) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, MultiColor color, TextEvent... textEvents) {
        return new TextComponent(text, color.getColorAsValue(), Arrays.asList(textEvents), new ArrayList<>());
    }
    
    public static TextComponent textComponent(String text, String color, Attribute... attributes) {
        return new TextComponent(text, color, new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, ChatColor color, Attribute... attributes) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, Attribute... attributes) {
        return new TextComponent(text, type.name(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, Color color, Attribute... attributes) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, MultiColor color, Attribute... attributes) {
        return new TextComponent(text, color.getColorAsValue(), new ArrayList<>(), Arrays.asList(attributes));
    }
    
    public static TextComponent textComponent(String text, String color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, color, textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, ChatColor color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, color, textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, ColorTheme.ColorType type, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, type.name(), textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, Color color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, new MultiColor(color).getColorAsValue(), textEvents, attributes);
    }
    
    public static TextComponent textComponent(String text, MultiColor color, List<TextEvent> textEvents, List<Attribute> attributes) {
        return new TextComponent(text, color.getColorAsValue(), textEvents, attributes);
    }
    
    public JSONObject translateJSON(ColorTheme theme) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(type, getText());
        jsonObject.put("color", translateColor(theme));
        if (!Strings.isNullOrEmpty(insertion)) jsonObject.put("insertion", insertion);
        
        for (Attribute attribute : Attribute.values()) {
            jsonObject.put(attribute.name(), (attributes.contains(attribute) ? "true" : "false"));
        }
        textEvents.forEach(event -> jsonObject.put(event.name(), event.translateJSON(theme)));
        
        if (!translateWith.isEmpty()) {
            JSONArray withMessages = new JSONArray();
            for (Message m : translateWith) {
                JSONArray withMessageComponents = new JSONArray();
                m.getText().stream().map(t -> t.translateJSON(theme)).forEach(withMessageComponents::add);
                withMessages.add(withMessageComponents);
            }
            jsonObject.put("with", withMessages);
        }
        
        return jsonObject;
    }
    
    private String translateColor(ColorTheme theme) {
        return Arrays.stream(ColorTheme.ColorType.values())
                .filter(type -> type.name().equalsIgnoreCase(color))
                .findFirst().map(type -> type.getColor(theme).getColorAsValue()).orElse(color);
    }
    
    public void clearEvents() {
        textEvents = new ArrayList<>();
    }
    
    public void clearInteractiveEvents() {
        if (textEvents != null) {
            textEvents = textEvents.stream()
                    .filter(event -> event.getClass().getAnnotation(TextEvent.InteractiveTextEvent.class) == null)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }
    
    public String getText() {
        if (pageNumber != null) {
            return text.replace(BookPage.getActivePageReplacer(), String.valueOf(pageNumber.getPageNumber()));
        }
        return text;
    }
    
    public String getRawText() {
        return text;
    }
    
    public TextComponent setText(String text) {
        this.text = text;
        return this;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(ChatColor color) {
        this.color = new MultiColor(color).getColorAsValue();
    }
    
    public void setColor(String color) {
        if (Arrays.stream(ColorTheme.ColorType.values()).anyMatch(type -> type.name().equalsIgnoreCase(color))) {
            this.color = color;
        } else {
            this.color = new MultiColor(color).getColorAsValue();
        }
    }
    
    public void setColor(Color color) {
        this.color = new MultiColor(color).getColorAsValue();
    }
    
    public void setColor(MultiColor color) {
        this.color = color.getColorAsValue();
    }
    
    public void setColor(ColorTheme.ColorType type) {
        this.color = type.name();
    }
    
    public List<Attribute> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(List<Attribute> attributes) {
        for (Attribute attribute : attributes)
            this.addAttribute(attribute);
    }
    
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }
    
    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }
    
    public List<TextEvent> getTextEvents() {
        return textEvents;
    }
    
    public <E extends TextEvent> E getTextEvent(Class<E> textEvent) {
        //noinspection unchecked
        return (E) textEvents.stream().filter(textEvent::isInstance).findFirst().orElse(null);
    }
    
    public ScoreEvent getScoreEvent() {
        return getTextEvent(ScoreEvent.class);
    }
    
    public ClickEvent getClickEvent() {
        return getTextEvent(ClickEvent.class);
    }
    
    public HoverEvent getHoverEvent() {
        return getTextEvent(HoverEvent.class);
    }
    
    public boolean hasTextEvent(Class<? extends TextEvent> textEvent) {
        return textEvents.stream().anyMatch(textEvent::isInstance);
    }
    
    public boolean hasScoreEvent() {
        return hasTextEvent(ScoreEvent.class);
    }
    
    public boolean hasClickEvent() {
        return hasTextEvent(ClickEvent.class);
    }
    
    public boolean hasHoverEvent() {
        return hasTextEvent(HoverEvent.class);
    }
    
    public <E extends TextEvent> E removeTextEvent(Class<E> textEvent) {
        TextEvent toRemove = textEvents.stream().filter(textEvent::isInstance).findFirst().orElse(null);
        if (toRemove != null) {
            textEvents.remove(toRemove);
        }
        return (E) toRemove;
    }
    
    public TextComponent addTextEvent(TextEvent textEvent) {
        removeTextEvent(textEvent.getClass());
        textEvents.add(textEvent);
        return this;
    }
    
    public String getType() {
        return type;
    }
    
    public TextComponent setType(String type) {
        this.type = type.toLowerCase();
        return this;
    }
    
    public TextComponent setType(TextType type) {
        this.type = type.name().toLowerCase();
        return this;
    }
    
    public String getInsertion() {
        return insertion;
    }
    
    public TextComponent setInsertion(String insertion) {
        this.insertion = insertion;
        return this;
    }
    
    public TextComponent setTextAsInsertion() {
        this.insertion = getText();
        return this;
    }
    
    public BookPage getBookPage() {
        return pageNumber;
    }
    
    public TextComponent setBookPage(BookPage pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }
    
    public TextComponent addTranslateWith(TextComponent... text) {
        for (TextComponent component : text) {
            this.addTranslateWith(new Message(component));
        }
        return this;
    }
    
    public TextComponent addTranslateWith(Message... messages) {
        translateWith.addAll(Arrays.asList(messages));
        return this;
    }
    
    public TextComponent removeTranslateWith(Message message) {
        translateWith.remove(message);
        return this;
    }
    
    public ArrayList<Message> getTranslateWith() {
        return translateWith;
    }
    
    @Override
    public Object clone() {
        TextComponent textComponent = new TextComponent(this.text, this.color);
        for (Attribute attribute : this.attributes) {
            textComponent.addAttribute(attribute);
        }
//        textComponent.attributes = new ArrayList<>(this.attributes);
        for (Message message : translateWith) textComponent.translateWith.add((Message) message.clone());
        textComponent.pageNumber = this.pageNumber;
        textComponent.insertion = this.insertion;
        textComponent.type = this.type;
        textComponent.textEvents = this.textEvents;
        return textComponent;
//        try {
//            return super.clone();
//        } catch (CloneNotSupportedException e) {
//        }
    }
}
