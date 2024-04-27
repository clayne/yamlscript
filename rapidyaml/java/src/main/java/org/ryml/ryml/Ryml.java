package org.ryml.ryml;

import com.sun.jna.ptr.PointerByReference;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Interface with the shared libryml library
 *
 */
public class Ryml
{
    public static String RYML_VERSION = "0.5.0";

    public static Object load(String ysCode)
    {
        return loadJSON(ysCode).get("data");
    }

    public static JSONObject loadObject(String ysCode)
    {
        return loadJSON(ysCode).getJSONObject("data");
    }

    public static JSONArray loadArray(String ysCode)
    {
        return loadJSON(ysCode).getJSONArray("data");
    }

    public static int loadInt(String ysCode)
    {
        return loadJSON(ysCode).getInt("data");
    }

    public static float loadFloat(String ysCode)
    {
        return loadJSON(ysCode).getFloat("data");
    }

    public static double loadDouble(String ysCode)
    {
        return loadJSON(ysCode).getDouble("data");
    }

    public static boolean loadBoolean(String ysCode)
    {
        return loadJSON(ysCode).getBoolean("data");
    }

    public static long loadLong(String ysCode)
    {
        return loadJSON(ysCode).getLong("data");
    }

    public static BigInteger loadBigInteger(String ysCode)
    {
        return loadJSON(ysCode).getBigInteger("data");
    }

    public static BigDecimal loadBigDecimal(String ysCode)
    {
        return loadJSON(ysCode).getBigDecimal("data");
    }

    public static JSONObject loadJSON(String code)
    {
        return new Ryml().evaluate(code);
    }

    private final ILibRyml libryml;

    public Ryml()
    {
        this.libryml = LibRyml.library();
    }

    public String getRAWResult(String ysCode) throws RuntimeException
    {
        PointerByReference threadRef = new PointerByReference();

        String jsonData = libryml.load_ys_to_json(threadRef.getValue(), ysCode);

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
