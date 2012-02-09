/**********************************************
 * Copyright (C) 2011 Lukas Laag
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
 * Opera version of IdRefTokenizer (required because Opera
 * transforms urls into absolute urls).
 * @author laaglu
 */
public class IdRefTokenizerOpera extends IdRefTokenizer {
	protected static final String START2 = "url(";
	protected static final String END2 = ")";

	public IdRefToken nextToken() {
		if (index1 != str.length()) {
			if (token.kind == IdRefToken.IDREF) {
				boolean notFirst = token.value != null;
				token.kind = IdRefToken.DATA;
				index2 = str.indexOf(START2, index1);
				if (index2 != -1) {
					token.value = str.substring(index1, index2 + START2.length()) + "'#";
					index1 = index2 + START2.length();
				} else {
					token.value = str.substring(index1);
					index1 = str.length();
				}
				if (notFirst) {
					token.value = "'" + token.value;
				}
			} else {
				index2 = str.indexOf(END2, index1);
				if (index2 != -1) {
					token.value = str.substring(index1, index2);
					int index3 = token.value.indexOf("#");
					if (index3 != -1) {
						token.value = token.value.substring(1 + index3);
					}
					if (token.value.endsWith("'")) {
						token.value = token.value.substring(0, token.value.length() - 1);
					}
					if (token.value.endsWith("\"")) {
						token.value = token.value.substring(0, token.value.length() - 1);
					}
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
