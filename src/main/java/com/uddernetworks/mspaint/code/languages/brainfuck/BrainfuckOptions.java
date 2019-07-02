package com.uddernetworks.mspaint.code.languages.brainfuck;

//public enum BrainfuckOptions implements Option {
//    INPUT_DIRECTORY("inputDirectory", REQUIRED),
//    HIGHLIGHT("highlight", BOTTOM_DISPLAY),
//    HIGHLIGHT_DIRECTORY("highlightDirectory", REQUIRED),
//    COMPILER_OUTPUT("compilerOutput", REQUIRED),
//    PROGRAM_OUTPUT("programOutput", REQUIRED);
//
//    private String name;
//    private LangGUIOptionRequirement requirement;
//
//    BrainfuckOptions(String name, LangGUIOptionRequirement requirement) {
//        this.name = name;
//        this.requirement = requirement;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public Option fromName(String name) {
//        return staticFromName(name);
//    }
//
//    public static BrainfuckOptions staticFromName(String name) {
//        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(JavaOptions.class, name));
//    }
//
//    public LangGUIOptionRequirement getRequirement() {
//        return this.requirement;
//    }
//}
