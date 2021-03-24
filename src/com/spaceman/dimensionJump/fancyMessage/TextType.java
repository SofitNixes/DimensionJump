package com.spaceman.dimensionJump.fancyMessage;

public enum TextType {
    TEXT,
    KEYBIND,
    TRANSLATE,
    SELECTOR;
    
    public boolean equals(String textType) {
        return this.name().equalsIgnoreCase(textType);
    }
}
