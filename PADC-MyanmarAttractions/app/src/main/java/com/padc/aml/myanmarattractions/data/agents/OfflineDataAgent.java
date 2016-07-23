package com.padc.aml.myanmarattractions.data.agents;

/**
 * Created by user on 7/9/2016.
 */
public class OfflineDataAgent implements AttractionDataAgent {
    @Override
    public void loadAttractions() {

    }

    @Override
    public void login(String email, String password) {

    }

    @Override
    public void register(String name, String email, String password, String dateOfBirth, String country) {

    }

    private static OfflineDataAgent objInstance;

    private OfflineDataAgent() {

    }
    public static OfflineDataAgent getInstance() {
        if (objInstance == null) {
            objInstance = new OfflineDataAgent();
        }
        return objInstance;
    }
}
