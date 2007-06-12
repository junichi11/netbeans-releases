/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.autoupdate.updateprovider;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.Module;
import org.netbeans.modules.autoupdate.services.FeatureUpdateElementImpl;
import org.netbeans.modules.autoupdate.services.Utilities;
import org.netbeans.spi.autoupdate.UpdateItem;
import org.netbeans.spi.autoupdate.UpdateProvider;
import org.openide.modules.ModuleInfo;
import org.openide.modules.SpecificationVersion;
import org.openide.util.NbBundle;

/**
 *
 * @author Jiri Rechtacek
 */
public class ArtificialFeaturesProvider implements UpdateProvider {
    private static final String UNSORTED_CATEGORY = NbBundle.getMessage (ArtificialFeaturesProvider.class, "ArtificialFeaturesProvider_Unsorted_Category");
    private static final String LIBRARIES_CATEGORY = NbBundle.getMessage (ArtificialFeaturesProvider.class, "ArtificialFeaturesProvider_Libraries_Category");
    private static final String BRIDGES_CATEGORY = NbBundle.getMessage (ArtificialFeaturesProvider.class, "ArtificialFeaturesProvider_Bridges_Category");
    private static final String FEATURES_CATEGORY = NbBundle.getMessage (ArtificialFeaturesProvider.class, "ArtificialFeaturesProvider_Features_Category");
        
    private final Collection<UpdateItem> originalItems;
    private static final Logger log = Logger.getLogger (ArtificialFeaturesProvider.class.getName ());
    
    public ArtificialFeaturesProvider (final Collection<UpdateItem> items) {
        originalItems = items;
    }

    public String getName () {
        return "artificial-module-provider"; // NOI18N
    }

    public String getDisplayName () {
        return getName ();
    }
    
    public String getDescription () {
        return null;
    }

    private static boolean generateArtificialFeatures () {
        String tmp = System.getProperty ("autoupdate.services.generate.features");
        return tmp == null || Boolean.valueOf (tmp);
    }

    public Map<String, UpdateItem> getUpdateItems () throws IOException {
        if (! generateArtificialFeatures ()) {
            return Collections.emptyMap ();
        }
        Map<String, UpdateItem> res = new HashMap<String, UpdateItem> ();
        
        // crate features built on installed modules
        Map<String, Set<ModuleInfo>> categoryToModules = new HashMap<String, Set<ModuleInfo>> ();
        for (UpdateItem item : originalItems) {
            UpdateItemImpl impl = Utilities.getUpdateItemImpl (item);
            if (impl instanceof InstalledModuleItem) {
                InstalledModuleItem installedModule = (InstalledModuleItem) impl;
                String category = (String) installedModule.getModuleInfo ().getLocalizedAttribute ("OpenIDE-Module-Display-Category");
                Module module = Utilities.toModule (installedModule.getModuleInfo ().getCodeNameBase (), installedModule.getModuleInfo ().getSpecificationVersion ().toString ());
                assert module != null : "Module found for " + installedModule.getModuleInfo ().getCodeNameBase () + ", " + installedModule.getModuleInfo ().getSpecificationVersion ();
                if (module.isAutoload () || module.isFixed ()) {
                    category = LIBRARIES_CATEGORY;
                    continue;
                } else if (module.isEager ()) {
                    category = BRIDGES_CATEGORY;
                    continue;
                } else if (category == null || category.length () == 0) {
                    category = UNSORTED_CATEGORY;
                }
                if (! categoryToModules.containsKey (category)) {
                    categoryToModules.put (category, new HashSet<ModuleInfo> ());
                }
                categoryToModules.get (category).add (installedModule.getModuleInfo ());
            } else if (impl instanceof ModuleItem) {
                ModuleItem updateModule = (ModuleItem) impl;
                String category = (String) updateModule.getModuleInfo ().getLocalizedAttribute ("OpenIDE-Module-Display-Category");
                if (LIBRARIES_CATEGORY.equals (category) || BRIDGES_CATEGORY.equals (category) || FEATURES_CATEGORY.equals (category)) {
                    continue;
                }
                if (category == null || category.length () == 0) {
                    String dn = (String) updateModule.getModuleInfo ().getLocalizedAttribute ("OpenIDE-Module-Display-Category");
                    if (dn == null || dn.length () == 0) {
                        category = UNSORTED_CATEGORY;
                    } else {
                        category = dn;
                    }
                }
                if (! categoryToModules.containsKey (category)) {
                    categoryToModules.put (category, new HashSet<ModuleInfo> ());
                }
                categoryToModules.get (category).add (updateModule.getModuleInfo ());
            } else {
                // XXX: ignore other types now
            }
        }
        
        // make a feature for each one category
        for (String category : categoryToModules.keySet ()) {
            FeatureItem featureItemImpl = createFeatureItem (category, categoryToModules.get (category), null);
            log.log (Level.FINE, "Create FeatureItem[" + category + ", " + featureItemImpl.getSpecificationVersion ().toString () +
                    "] containing modules " + featureItemImpl.getDependenciesToModules ());
            UpdateItem featureItem = Utilities.createUpdateItem (featureItemImpl);
            res.put (featureItemImpl.getCodeName () + '_' + featureItemImpl.getSpecificationVersion (), featureItem);
        }
        
        return res;
    }

    public boolean refresh (boolean force) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static FeatureItem createFeatureItem (String codeName, Set<ModuleInfo> modules, FeatureUpdateElementImpl original) {
        Set<String> containsModules = new HashSet<String> ();
        String versionN = "";
        String descriptionN = "";
        for (ModuleInfo info : modules) {
            containsModules.add (info.getCodeNameBase () + " = " + info.getImplementationVersion ());
            SpecificationVersion spec = info.getSpecificationVersion ();
            versionN = addVersion (versionN, spec);
            descriptionN += "<h5>" + info.getDisplayName () + "</h5>";
            String desc = (String) info.getLocalizedAttribute ("OpenIDE-Module-Long-Description");
            descriptionN += desc == null ? "" : desc; // NOI18N
        }
        
        String description = original == null || original.getDescription () == null || original.getDescription ().length () == 0 ? descriptionN :
            original.getDescription ();

        String displayName = original == null || original.getDisplayName () == null || original.getDisplayName ().length () == 0 ? codeName :
            original.getDisplayName ();

        String version = original == null || original.getSpecificationVersion() == null? versionN :
            original.getSpecificationVersion ().toString ();

        return new FeatureItem (codeName, version, containsModules, displayName, description, null);
    }
    
    // XXX: should be move somewhere into utils
    public static String createVersion (Collection<ModuleInfo> modules) {
        String version = "";
        for (ModuleInfo info : modules) {
            SpecificationVersion spec = info.getSpecificationVersion ();
            version = addVersion (version, spec);
        }
        return version;
    }


    private static String addVersion (String version, SpecificationVersion spec) {
        int [] addend1 = getDigitsInVersion (version);
        int [] addend2 = getDigitsInVersion (spec.toString ());
        
        int length = Math.max (addend1.length, addend2.length);
        int [] result = new int [length];
        
        for (int i = 0; i < result.length; i++) {
            assert i < addend1.length || i < addend2.length;
            int digit = 0;
            if (i < addend1.length) {
                digit += addend1 [i];
            }
            if (i < addend2.length) {
                digit += addend2 [i];
            }
            result [i] = digit;
        }
        
        StringBuilder buf = new StringBuilder ((result.length * 3) + 1);

        for (int i = 0; i < result.length; i++) {
            if (i > 0) {
                buf.append ('.'); // NOI18N
            }

            buf.append (result [i]);
        }

        return buf.toString();
    }
    
    private static int [] getDigitsInVersion (String version) {
        if (version.length () == 0) {
            return new int [0];
        }
        StringTokenizer tok = new StringTokenizer (version, ".", true); // NOI18N
        
        int len = tok.countTokens ();
        assert (len % 2) != 0 : "Even number of pieces in a spec version: `" + version + "`";
        
        int[] digits = new int[len / 2 + 1];
        int i = 0;

        boolean expectingNumber = true;

        while (tok.hasMoreTokens ()) {
            String toParse = tok.nextToken ();
            if (expectingNumber) {
                expectingNumber = false;

                try {
                    int piece = Integer.parseInt (toParse);
                    assert piece >= 0 : "Spec version component < 0: " + piece;
                    digits[i++] = piece;
                } catch (NumberFormatException nfe) {
                    log.log (Level.INFO, "NumberFormatException while parsing " + version, nfe);
                }
                
            } else {
                assert ".".equals (toParse) : "Expected dot in spec version: `" + version + "'";

                expectingNumber = true;
            }
        }
        
        return digits;
    }

}
