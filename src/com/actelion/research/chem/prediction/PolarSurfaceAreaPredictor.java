/*
 * Copyright 2014 Actelion Pharmaceuticals Ltd., Gewerbestrasse 16, CH-4123 Allschwil, Switzerland
 *
 * This file is part of DataWarrior.
 * 
 * DataWarrior is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * DataWarrior is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with DataWarrior.
 * If not, see http://www.gnu.org/licenses/.
 *
 * @author Thomas Sander
 */

package com.actelion.research.chem.prediction;

import com.actelion.research.chem.Molecule;
import com.actelion.research.chem.StereoMolecule;


public class PolarSurfaceAreaPredictor {
	public static final float cPSAUnknown = -1.0f;

	private static final String[] cAtomTypeName = {
		"[N](-*)(-*)-*",
		"[N](-*)=*",
		"[N]#*",
		"[N](-*)(=*)=* as in nitro",
		"[N](=*)#* middle atom of azide",
		"[N]1(-*)-*-*-1 3-membered ring",
		"[NH](-*)-*",
		"[NH]1-*-*-1 3-membered ring",
		"[NH]=*",
		"[NH2]-*",
		"[N+](-*)(-*)(-*)-*",
		"[N+](-*)(-*)=*",
		"[N+](-*)#* N in isocyano",
		"[NH+](-*)(-*)-*",
		"[NH+](-*)=*",
		"[NH2+](-*)-*",
		"[NH2+]=*",
		"[NH3+]-*",
		"[n](:*):*",
		"[n](:*)(:*):*",
		"[n](-*)(:*):*",
		"[n](=*)(:*):* as in pyridine-N-oxid",
		"[nH](:*):*",
		"[n+](:*)(:*):*",
		"[n+](-*)(:*):*",
		"[nH+](:*):*",
		"[O](-*)-*",
		"[O]1-*-*-1 3-membered ring",
		"[O]=*",
		"[OH]-*",
		"[O-]-*",
		"[o](:*):*",
		"[S](-*)-*",
		"[S]=*",
		"[S](-*)(-*)=*",
		"[S](-*)(-*)(=*)=*",
		"[SH]-*",
		"[s](:*):*",
		"[s](=*)(:*):*",
		"[P](-*)(-*)-*",
		"[P](-*)=*",
		"[P](-*)(-*)(-*)=*",
		"[PH](-*)(-*)=*"
		};

	private static final float[] cIncrement = {
	   3.24f,  12.36f,  23.79f,  11.68f,  13.60f,   3.01f,  12.03f,  21.94f,
	  23.85f,  26.02f,   0.00f,   3.01f,   4.36f,   4.44f,  13.97f,  16.61f,
	  25.59f,  27.64f,  12.89f,   4.41f,   4.93f,   8.39f,  15.79f,   4.10f,
	   3.88f,  14.14f,   9.23f,  12.53f,  17.07f,  20.23f,  23.06f,  13.14f,
	  25.30f,  32.09f,  19.21f,   8.38f,  38.80f,  28.24f,  21.70f,  13.59f,
	  34.14f,   9.81f,  23.47f };

	public PolarSurfaceAreaPredictor() {
		}


	public float assessPSA(StereoMolecule mol) {
		int[] count = getAtomTypeCounts(mol);

		float psa = 0.0f;
		for (int i=0; i<cIncrement.length; i++)
			psa += count[i] * cIncrement[i];

		return psa;
		}


	public ParameterizedStringList getDetail(StereoMolecule mol) {
		ParameterizedStringList detail = new ParameterizedStringList();
		detail.add("The polar surface area prediction is based on an atom-type based",
							ParameterizedStringList.cStringTypeText);
		detail.add("increment system, published by P. Ertl, B. Rohde, P. Selzer",
							ParameterizedStringList.cStringTypeText);
		detail.add("in J. Med. Chem. 2000, 43, 3714-3717",
							ParameterizedStringList.cStringTypeText);
		detail.add("Recognized atom types and their contributions are:",
							ParameterizedStringList.cStringTypeText);

		int[] count = getAtomTypeCounts(mol);

		for (int i=0; i<cIncrement.length; i++)
			if (count[i] != 0)
				detail.add(""+count[i]+" * "+cIncrement[i]+"   AtomType: "
								  +cAtomTypeName[i],ParameterizedStringList.cStringTypeText);

		return detail;
		}


	private int[] getAtomTypeCounts(StereoMolecule mol) {
		int[] count = new int[cIncrement.length+2];

		mol.ensureHelperArrays(Molecule.cHelperRings);

		for (int atom=0; atom<mol.getAtoms(); atom++)
			count[getAtomType(mol, atom)]++;

		return count;
		}


	private int getAtomType(StereoMolecule mol, int atom) {
		switch (mol.getAtomicNo(atom)) {
		case 7:
			if (mol.isAromaticAtom(atom)) {
				if (mol.getAtomCharge(atom) == 0) {
					if (mol.getAllHydrogens(atom) == 0) {
						if (mol.getConnAtoms(atom) == 2)
							return 18;
						else {
							for (int i=0; i<mol.getConnAtoms(atom); i++)
								if (!mol.isAromaticBond(mol.getConnBond(atom, i)))
									return 20;
							return 19;
							}
						}
					else	// hydrogens > 0
						return 22;
					}
				else if (mol.getAtomCharge(atom) == 1) {
					if (mol.getAllHydrogens(atom) == 0) {
						for (int i=0; i<mol.getConnAtoms(atom); i++)
							if (!mol.isAromaticBond(mol.getConnBond(atom, i)))
								return (mol.getAtomCharge(mol.getConnAtom(atom, i)) < 0) ? 21 : 24;
						return 23;
						}
					else	// hydrogens > 0
						return 25;
					}
				}
			else {	// not aromatic
				if (mol.getAtomCharge(atom) == 0) {
					switch (mol.getAllHydrogens(atom)) {
					case 0:	// hydrogens
						switch (mol.getAtomPi(atom)) {
						case 0:	// pi
							if (mol.getAtomRingSize(atom) == 3)
								return 5;
							else
								return 0;
						case 1:	// pi
							return 1;
						case 2:	// pi
							return 2;
							}
						break;
					case 1:	// hydrogens
						switch (mol.getAtomPi(atom)) {
						case 0:	// pi
							if (mol.getAtomRingSize(atom) == 3)
								return 7;
							else
								return 6;
						case 1:	// pi
							return 8;
							}
						break;
					case 2:	// hydrogens
						return 9;
						}
					}
				else if (mol.getAtomCharge(atom) == 1) {
					switch (mol.getAllHydrogens(atom)) {
					case 0:	// hydrogens
						switch (mol.getAtomPi(atom)) {
						case 0:	// pi
							return 10;
						case 1:	// pi
							return hasNegativeNeighbour(mol, atom) ? 3 : 11;
						case 2:	// pi
							if (mol.getConnBondOrder(atom, 0) == 2)
								return hasNegativeNeighbour(mol, atom) ? 4 : cIncrement.length+1;
							else
								return 12;
							}
						break;
					case 1:	// hydrogens
						switch (mol.getAtomPi(atom)) {
						case 0:	// pi
							return 13;
						case 1:	// pi
							return 14;
							}
						break;
					case 2:	// hydrogens
						return (mol.getAtomPi(atom) == 0) ? 15 : 16;
					case 3:	// hydrogens
						return 17;
						}
					}
				}
			return cIncrement.length+1;	// unrecognized N
		case 8:
			if (mol.isAromaticAtom(atom)) {
				if (mol.getAtomCharge(atom) == 0)
					return 31;
				}
			else {
				if (mol.getAtomCharge(atom) == 0) {
					if (mol.getAtomPi(atom) > 0)
						return 28;
					if (mol.getConnAtoms(atom) == 1)
						return 29;
					if (mol.getAtomRingSize(atom) == 3)
						return 27;
					return 26;
					}
				else if (mol.getAtomCharge(atom) == -1) {
					if (mol.getConnAtoms(atom) == 1
					 && mol.getAtomCharge(mol.getConnAtom(atom, 0)) > 0)
						return 28;	// return -[O-] of nitro as =O
					return 30;
					}
				}
			return cIncrement.length+1;	// unrecognized O
		case 15:
			if (mol.getAtomCharge(atom) == 0) {
				if (mol.getAllHydrogens(atom) == 0) {
					if (mol.getConnAtoms(atom) == 3
					 && mol.getAtomPi(atom) == 0)
						return 39;
					if (mol.getConnAtoms(atom) == 2
					 && mol.getAtomPi(atom) == 1)
						return 40;
					if (mol.getConnAtoms(atom) == 4
					 && mol.getAtomPi(atom) == 1)
						return 41;
					}
				else if (mol.getAllHydrogens(atom) == 1) {
					if (mol.getConnAtoms(atom) == 3
					 && mol.getAtomPi(atom) == 1)
						return 42;
					}
				}
			return cIncrement.length+1;	// unrecognized P
		case 16:
			if (mol.getAtomCharge(atom) == 0) {
				if (mol.isAromaticAtom(atom)) {
					if (mol.getConnAtoms(atom) == 2)
						return 37;
					else
						return 38;
					}
				else {
					if (mol.getAllHydrogens(atom) == 0) {
						if (mol.getConnAtoms(atom) == 2
						 && mol.getAtomPi(atom) == 0)
							return 32;
						if (mol.getConnAtoms(atom) == 1
						 && mol.getAtomPi(atom) == 1)
							return 33;
						if (mol.getConnAtoms(atom) == 3
						 && mol.getAtomPi(atom) == 1)
							return 34;
						if (mol.getConnAtoms(atom) == 4
						 && mol.getAtomPi(atom) == 2)
							return 35;
						}
					else if (mol.getAllHydrogens(atom) == 1) {
						if (mol.getConnAtoms(atom) == 1)
							return 36;
						}
					}
				}
			return cIncrement.length+1;	// unrecognized S
			}

		return cIncrement.length;	// undefined type
		}


	private boolean hasNegativeNeighbour(StereoMolecule mol, int atom) {
		for (int i=0; i<mol.getConnAtoms(atom); i++)
			if (mol.getAtomCharge(mol.getConnAtom(atom, i)) < 0)
				return true;

		return false;
		}
	}
