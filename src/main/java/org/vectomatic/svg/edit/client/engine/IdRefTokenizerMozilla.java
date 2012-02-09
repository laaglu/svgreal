/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of vectomatic2.
 * 
 * vectomatic2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * vectomatic2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vectomatic2.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client.engine;

/**
 * FF version of IdRefTokenizer (required because FF does
 * inconsistently add quotes around id refs).
 * @author laaglu
 */
public class IdRefTokenizerMozilla extends IdRefTokenizer {
	protected static final String START2 = "url(\"#";
	protected static final String END2 = "\")";
	private boolean hasQuotes;

	public IdRefToken nextToken() {
		if (index1 != str.length()) {
			if (token.kind == IdRefToken.IDREF) {
				token.kind = IdRefToken.DATA;
				index2 = str.indexOf(START2, index1);
				if (index2 != -1) {
					hasQuotes = true;
					token.value = str.substring(index1, index2 + START2.length());
					index1 = index2 + START2.length();
				} else {
					index2 = str.indexOf(START, index1);
					if (index2 != -1) {
						hasQuotes = false;
						token.value = str.substring(index1, index2 + START.length());
						index1 = index2 + START.length();
					} else {
						token.value = str.substring(index1);
						index1 = str.length();
					}
				}
			} else {
				if (hasQuotes) {
					index2 = str.indexOf(END2, index1);
				} else {
					index2 = str.indexOf(END, index1);
				}
				if (index2 != -1) {
					token.value = str.substring(index1, index2);
					index1 = index2;
					token.kind = IdRefToken.IDREF;
				} else {
					token.value = str.substring(index1);
					index1 = str.length();
				}
			}
			return token;
		}
		return null;
	}
}
