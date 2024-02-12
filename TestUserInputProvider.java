package com.example;

public class TestUserInputProvider implements UserInputProvider {

    private String simulatedInput;

    public void setSimulatedInput(String simulatedInput) {
        this.simulatedInput = simulatedInput;
    }

    @Override
    public String promptUserForContainerId() {
        return simulatedInput;
    }
}
