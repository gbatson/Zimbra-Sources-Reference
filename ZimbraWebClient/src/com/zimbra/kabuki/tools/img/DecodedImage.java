/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


package com.zimbra.kabuki.tools.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Pattern;

public abstract class DecodedImage {
	private static final Pattern sBACKSLASH = Pattern.compile("\\\\");
	private static final String IMG_RELATIVE_PATH = "img" + File.separatorChar;
	protected String mFilename;
    protected File mInputDir;
    protected int mCombinedRow = -1;
    protected int mCombinedColumn = -1;
    protected String mPrefix;
    protected String mSuffix;
    
    protected int mLayoutStyle;

    public abstract BufferedImage getBufferedImage();

    public abstract int getWidth();

    public abstract int getHeight();

    public void setCombinedRow(int r) {
        mCombinedRow = r;
    }

    public int getCombinedRow() {
        return mCombinedRow;
    }

    public void setCombinedColumn(int c) {
        mCombinedColumn = c;
    }

    public int getCombinedColumn() {
        return mCombinedColumn;
    }

    public int getLayoutStyle() {
        return mLayoutStyle;
    }

    /*
     * Get a CSS definition for this piece of the combined image.
     * expects combinedFilename to be of the form "megaimage.gif".
     */
    public String getCssString(int combinedWidth,
                               int combinedHeight,
                               String combinedFilename,
                               boolean includeDisableCss) {
        return getCssString(combinedWidth, combinedHeight, combinedFilename, includeDisableCss, false);
    }

    public String getCssString(int combinedWidth,
                               int combinedHeight,
                               String combinedFilename,
                               boolean includeDisableCss,
                               boolean unmerged) {
        String fileNameBase = this.getName();

        // background image
        String bgImgStr = mPrefix + (unmerged ? getNameAfterBase(mFilename) : combinedFilename) + "?v=@jsVersion@";

        // background position
        String bgPosStr = (unmerged)
                            ? "0px 0px"
                            : getBgPosition();

		// background repeat
        // NOTE: Images that are explicitly laid out horizontally are used as
        //		 vertical borders and should y-repeat. Likewise, images laid
        //		 out vertically are used for horizontal borders and should
        //		 x-repeat. All other images should be set no-repeat, unless
        //		 explicitly set as a repeat layout.
        String bgRptStr = "no-repeat";
        switch (getLayoutStyle()) {
            case ImageMerge.HORIZ_LAYOUT:
                bgRptStr = "repeat-x";
                break;
            case ImageMerge.VERT_LAYOUT:
                bgRptStr = "repeat-y";
                break;
            case ImageMerge.TILE_LAYOUT:
                bgRptStr = "repeat";
                break;
        }

		// width
        String widthStr = getLayoutStyle() != ImageMerge.HORIZ_LAYOUT && getLayoutStyle() != ImageMerge.TILE_LAYOUT
                ? "width:" + getWidth() + "px !important;" : "";
		
		// height
        String heightStr = getLayoutStyle() != ImageMerge.VERT_LAYOUT && getLayoutStyle() != ImageMerge.TILE_LAYOUT
                ? "height:" + getHeight() + "px !important;" : "";

		StringBuffer buffer = new StringBuffer();
	    String[] namePieces = fileNameBase.split("-");
	    for (String p : namePieces) {
		    buffer.append(" .");
		    buffer.append(p);
	    }

	    // CSS selector (may be further modified below)
        String selector = buffer.toString();

		// body of the style definition
		// NOTE:	IE doesn't process PNG graphics normally, so we output PNGs with
		//			the filter attribute in IE (using the #IFDEF syntax to make sure that
		//			it only shows up for IE)
		String styleBody;
        if (mSuffix.equalsIgnoreCase("png")) {
            styleBody = "\n" +
                "#IFDEF MSIE\n" +
                    "filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + bgImgStr + "',sizingMethod='image');\n" +
                "#ELSE\n" +
                    "background: url('"+bgImgStr+"') " + bgPosStr + " " + bgRptStr + ";\n" +
                "#ENDIF\n"
            ;
		} else {
			styleBody = "background:url(\"" + bgImgStr + "\") " + bgPosStr + " " + bgRptStr + ";"
							+ widthStr + heightStr + "overflow:hidden;";
		}

        if (includeDisableCss) {
            return selector + "," + selector + "Dis" + "{" + styleBody + "}\n" 
            	 + selector + "Dis" + "{opacity:.3;\n#IFDEF MSIE\nfilter:alpha(opacity=30);\n#ENDIF\n}";
        } else {
            return selector + "{" + styleBody + "}";
        }
    }

    public abstract void load() throws java.io.IOException, ImageMergeException;

	public String getName() {
		String fileName = this.getFilename();
		String fileNameBase = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

		// Strip the extension.
	    fileNameBase = fileNameBase.substring(0, fileNameBase.lastIndexOf('.'));

		// Strip any "repeat*" tiling derectives.  (Static layout has no directive.)
		if (fileNameBase.endsWith(ImageMerge.LAYOUT_EXTENSIONS[ImageMerge.HORIZ_LAYOUT])
				|| fileNameBase.endsWith(ImageMerge.LAYOUT_EXTENSIONS[ImageMerge.VERT_LAYOUT])
				|| fileNameBase.endsWith(ImageMerge.LAYOUT_EXTENSIONS[ImageMerge.TILE_LAYOUT])) {
			fileNameBase = fileNameBase.substring(0, fileNameBase.lastIndexOf('.'));
		}

		return fileNameBase;
	}

	public int getTop() {
		return mCombinedRow;
	}
	
	public int getLeft() {
		return mCombinedColumn;
	}

    public String getFilename() {
        return mFilename;
    }


    //
    // Protected
    //

    protected String getBgPosition() {
        return ((mCombinedColumn == 0) ? "" : "-") + mCombinedColumn + "px " +
               ((mCombinedRow == 0) ? "" : "-") + mCombinedRow + "px";
    }

    //
    // Private
    //

    /*
     * Get the relavent part of the image name, the part after "img/".
     */
    private static String getNameAfterBase(String fullname) {
        int i = fullname.lastIndexOf(IMG_RELATIVE_PATH);
        if (i == -1) {
            return null;
        }
		String relativePath = fullname.substring(i+4);
		return sBACKSLASH.matcher(relativePath).replaceAll("/");
    }
}