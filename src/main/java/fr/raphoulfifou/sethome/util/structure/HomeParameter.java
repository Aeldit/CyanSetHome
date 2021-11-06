package fr.raphoulfifou.sethome.util.structure;

import java.util.List;

public class HomeParameter {
    public List<Parameters> parameters;

    /**
     * <p>Used when a home is created to store its parameters</p>
     * <p>In the json file, contains the {@link Parameters Parameters}</p>
     */
    public HomeParameter(List<Parameters> parameters) {
        this.parameters = parameters;
    }
}
