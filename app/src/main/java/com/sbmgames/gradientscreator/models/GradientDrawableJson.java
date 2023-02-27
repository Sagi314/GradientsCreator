package com.sbmgames.gradientscreator.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GradientDrawableJson
{
    private static final String COLORS = "colors";
    private static final String TYPE = "type";
    private static final String DIRECTION = "direction";

    private int[] colorsList;
    private int typeIndex;
    private int directionIndex;

    public GradientDrawableJson(int[] colorsList, int typeIndex, int directionIndex)
    {
        this.colorsList = colorsList;
        this.typeIndex = typeIndex;
        this.directionIndex = directionIndex;
    }

    //region GETTERS
    public int[] getColorsList()
    {
        return colorsList;
    }

    public int getTypeIndex()
    {
        return typeIndex;
    }

    public int getDirectionIndex()
    {
        return directionIndex;
    }
    //endregion

    //region SETTERS
    public void setColorsList(int[] colorsList)
    {
        this.colorsList = colorsList;
    }

    public void setTypeIndex(int typeIndex)
    {
        this.typeIndex = typeIndex;
    }

    public void setDirectionIndex(int directionIndex)
    {
        this.directionIndex = directionIndex;
    }
    //endregion

    public String toJson()
    {
        return json(colorsList, typeIndex, directionIndex);
    }

    public static String json(int[] colorsList, int typeIndex, int directionIndex)
    {
        try
        {
            var gradientDrawableJsonObject = new JSONObject();

            var colorsJsonObject = new JSONArray(colorsList);
            gradientDrawableJsonObject.put(COLORS, colorsJsonObject);

            gradientDrawableJsonObject.put(TYPE, typeIndex);

            gradientDrawableJsonObject.put(DIRECTION, directionIndex);

            return gradientDrawableJsonObject.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}