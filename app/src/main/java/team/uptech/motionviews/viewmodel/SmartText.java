package team.uptech.motionviews.viewmodel;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;

import java.util.ArrayList;

import team.uptech.motionviews.utils.FontProvider;

public class SmartText {

    private FontProvider fontProvider = null;
    private ArrayList<SmartLine> lines;
    private float size = Float.MIN_VALUE;
    private Typeface typeface = null;
    private int maxWidth = Integer.MIN_VALUE;
    private int calculatedMaxWidth =  Integer.MIN_VALUE;

    public SmartText(String text, float size, Typeface typeface, int maxWidth) {
        lines = new ArrayList<>();
        setSmartText(text, size, typeface, maxWidth);
    }

    public boolean setSmartText (String text, float size, Typeface typeface, int maxWidth) {
        if (text == null || size < 0 || typeface == null || maxWidth < 0) {
            return false;
        }

        String [] stringLines = text.split("\n");
        int maxLineWidth = Integer.MIN_VALUE;
        for(String line : stringLines) {
            int lineWidth = measureTextWidth(line);
            if (lineWidth > maxWidth) {
                // Split lines and check line width
                ArrayList<SmartLine> splitLines = splitLine(line, lineWidth);
                maxLineWidth = maxWidth;
                lines.addAll(splitLines);
            } else {
                if (lineWidth > maxLineWidth) {
                    maxLineWidth = lineWidth;
                }
                lines.add(new SmartLine(line, lineWidth));
            }
        }

        calculatedMaxWidth = maxLineWidth;
        return true;
    }

    private boolean update() {
        if (size < 0 || typeface == null || maxWidth < 0) {
           return false;
        }
        return setSmartText(getText(true), size, typeface, maxWidth);
    }

    private ArrayList<SmartLine> splitLine (String line, int lineWidth) {
        ArrayList<SmartLine> splitLines = new ArrayList<>();
        float ratio = lineWidth / maxWidth;
        int start = 0, end = 0;
        for (int i = 0; i < (int)ratio; i++) {
            // TODO: add lines with maxwidth --> calc average character width, calc position, check position, add characters until limit reached, substring, do same for next block
            // splitLines.add(new SmartLine(subString, true, subWidth)); // soft line break
        }

        if (ratio % (int)ratio > 0) {
            String subString = line.substring(start);
            int subWidth = measureTextWidth(subString);
            splitLines.add(new SmartLine(subString, subWidth)); // real line break
        }

        return splitLines;
    }

    private int measureTextWidth (String s) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(size);
        textPaint.setTypeface(typeface);
        return (int)(textPaint.measureText(s) + 0.5f);
    }

    public void setSmartText (SmartText smartText) {
        lines = smartText.getLinesArray();
        size = smartText.getSize();
        typeface = smartText.getTypeface();
        maxWidth = smartText.getMaxWidth();
        calculatedMaxWidth = smartText.getCalculatedMaxWidth();
    }

    public boolean setText(String text) {
        if (size > 0 && maxWidth > 0 && typeface != null) {
            return setSmartText(text, size, typeface, maxWidth);
        }
        return false;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        update();
    }

    public void setSize(float size) {
        this.size = size;
        update();
    }

    public ArrayList<SmartLine> getLinesArray() {
        return lines;
    }

    public float getSize() {
        return size;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public int getCalculatedMaxWidth() {
        return calculatedMaxWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public String getText(boolean ignoreSoftBreakes) {
        if (ignoreSoftBreakes) {
            if (lines != null && lines.size() > 0) {
                String output = "";
                for (SmartLine line : lines) {
                    output += line.getText() + (!line.getIsSoftBreak() ? "\n" : "");
                }
                return output;
            }
            return "";
        }
        return toString();
    }

    public String toString() {
        if (lines != null && lines.size() > 0) {
            String output = "";
            for (SmartLine line : lines) {
                output += line.getText() + "\n";
            }
            return output;
        }
        return "";
    }

    // SMART LINE CLASS
    private class SmartLine {
        private String text = null;
        private boolean isSoftBreak;
        private int lineWidth = Integer.MIN_VALUE;

        public SmartLine(String text, int lineWidth, boolean isSoftBreak) {
            this.text = text;
            this.isSoftBreak = isSoftBreak;
            this.lineWidth = lineWidth;
        }

        public SmartLine(String text, int lineWidth) {
            this(text, lineWidth, false);
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean getIsSoftBreak() {
            return isSoftBreak;
        }

        public void setSoftBreak(boolean softBreak) {
            isSoftBreak = softBreak;
        }

        public int getLineWidth() {
            return lineWidth;
        }

        public void setLineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
        }
    }
}
