package org.ryml.ryml;

/**
 * Interface with the shared libryml library
 *
 */
public class Ryml
{
    public static String RYML_VERSION = "0.5.0";

    private final ILibRyml libryml;

    public Ryml()
    {
        this.libryml = LibRyml.library();
    }

    public String getRAWResult(String ysCode) throws RuntimeException
    {
        String jsonData = libryml.parse_yamlscript_to_events(ysCode);
        System.out.println("foooo!! " + jsonData);
        return jsonData;
    }

    public JSONObject evaluate(String ysCode)
    {
        JSONObject jsonData = new JSONObject(getRAWResult(ysCode));

        if (jsonData.has("error") && !jsonData.isNull("error")) {
            String error = jsonData.getString("error");
            throw new RuntimeException(error);
        }

        return jsonData;
    }

}
