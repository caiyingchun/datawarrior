/*
 * Project: DD_core
 * @(#)LittleEndianDataInputStream.java
 *
 * Copyright (c) 1997- 2014
 * Actelion Pharmaceuticals Ltd.
 * Gewerbestrasse 16
 * CH-4123 Allschwil, Switzerland
 *
 * All Rights Reserved.
 *
 * This software is the proprietary information of Actelion Pharmaceuticals, Ltd.
 * Use is subject to license terms.
 *
 */

package com.actelion.research.util;

import java.io.*;


public class LittleEndianDataInputStream implements DataInput
{
	public LittleEndianDataInputStream(InputStream in)
	{
		this.in = in;
		this.d = new DataInputStream(in);
		w = new byte[8];
	}

	public final short readShort() throws IOException
	{
		d.readFully(w, 0, 2);

		return (short)(((w[1] & 0xff) << 8) | (w[0] & 0xff));
	}

	public final int readUnsignedShort() throws IOException
	{
		d.readFully(w, 0, 2);

		return (((w[1] & 0xff) << 8) | (w[0] & 0xff));
	}

	public final char readChar() throws IOException
	{
		d.readFully(w, 0, 2);

		return (char)(((w[1] & 0xff) << 8) | (w[0] & 0xff));
	}

	public final int readInt() throws IOException
	{
		d.readFully(w, 0, 4);

		return ((w[3]) << 24) | ((w[2] & 0xff) << 16) | ((w[1] & 0xff) << 8) | (w[0] & 0xff);
	}

	public final long readLong() throws IOException
	{
		d.readFully(w, 0, 8);

		return ((long)(w[7]) << 56) /* long cast needed or shift done modulo 32 */
		| ((long)(w[6] & 0xff) << 48) | ((long)(w[5] & 0xff) << 40) | ((long)(w[4] & 0xff) << 32)
		| ((long)(w[3] & 0xff) << 24) | ((long)(w[2] & 0xff) << 16) | ((long)(w[1] & 0xff) << 8)
		| (long)(w[0] & 0xff);
	}


	public final float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInt());
	}

	public final double readDouble() throws IOException
	{
		return Double.longBitsToDouble(readLong());
	}


	public final int read(byte[] b, int off, int len) throws IOException
	{
		return in.read(b, off, len);
	}

	public final void readFully(byte[] b) throws IOException
	{
		d.readFully(b, 0, b.length);
	}

	public final void readFully(byte[] b, int off, int len)
		throws IOException
	{
        if (len < 0)
            System.err.println("Error: Negative number of bytes to read");
		else
            d.readFully(b, off, len);
	}


	public final int skipBytes(int n) throws IOException
	{
		return d.skipBytes(n);
	}

	public final boolean readBoolean() throws IOException
	{
		return d.readBoolean();
	}

	public final byte readByte() throws IOException
	{
		return d.readByte();
	}

	public final int readUnsignedByte() throws IOException
	{
		return d.readUnsignedByte();
	}

	@Deprecated
	public final String readLine() throws IOException
	{
		return d.readLine();
	}

	public final String readUTF() throws IOException
	{
		return d.readUTF();
	}

	public static String readUTF(DataInput in) throws IOException
	{
		return DataInputStream.readUTF(in);
	}

	public final void close() throws IOException
	{
		d.close();
	}

	protected DataInputStream d; // to get at high level readFully methods of DataInputStream
	protected InputStream in; // to get at the low-level read methods of InputStream
	byte[] w; // work array for buffering input

}
