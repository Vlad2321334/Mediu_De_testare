package sdf;

/**
 * Product categories with their corresponding codes
 * Organized according to the new weighing application requirements
 */
public enum ProductCategory {
    // FRIGIDERE main category
    FRIGIDERE("FRIGIDERE", true, null),
    ECHIPAMENTE_FRIGORIFICE("Echipamente frigorifice", false, "3011.34898"),
    ECHIPAMENTE_FRIGORIFICE_FARA_MOTOR("Echipamente frigorifice fara motor", false, "3011.34899"),
    FRIGIDERE_AMONIAC("Frigidere amoniac", false, "3011.34905"),
    
    // VITRINE FRIG. main category
    VITRINE_FRIG("VITRINE FRIG.", true, null),
    VITRINE_FRIGORIFICE("Vitrine frigorifice", false, "3011.34896"),
    VITRINE_FRIGORIFICE_FARA_MOTOR("vitrine frigorifice fara motor", false, "3011.34897"),
    ALTE_APARATE_MARI_REFRIGERARE("Alte aparate mari utiliz pt refrigerare", false, "3011.29822"),
    
    // DULAPI SPUMATI (ARCTIC) main category
    DULAPI_SPUMATI("DULAPI SPUMATI (ARCTIC)", true, null),
    DULAPI_SPUMATI_FS_390("DULAPI SPUMATI FS 390", false, "3011.31482"),
    DULAPI_SPUMATI_CF("DULAPI SPUMATI CF", false, "3011.31484"),
    USI_SPUMATE_FS_CF("USI SPUMATE FS+CF", false, "3011.31485"),
    
    // AC main category
    AC("AC", true, null),
    ECHIPAMENTE_AER_CONDITIONAT("Echipamente de aer conditionat", false, "3011.34907"),
    ECHIPAMENTE_AER_CONDITIONAT_CTG_B("Echipamente de aer conditionat ctg B", false, "3011.38199"),
    
    // DOZATOARE main category
    DOZATOARE("DOZATOARE", true, null),
    DISTRIBUITOARE_AGENT_FRIGORIFIC("Distribuitoare cu agent frigorific", false, "3011.34908"),
    
    // CALORIFERE ELECTRICE main category
    CALORIFERE_ELECTRICE("CALORIFERE ELECTRICE", true, null),
    ALTE_ECHIPAMENTE_TRANSFER_TERMIC("Alte echipamente de transfer termic", false, "3011.39010"),
    APARATE_ELECTRICE_INCALZIT("aparate electrice de incalzit", false, "3011.30125"),
    APARATE_ELECTRICE_INCALZIT_BOILERE("Aparate electrice de incalzit si boilere", false, "3011.34906");
    
    private final String displayName;
    private final boolean isMainCategory;
    private final String productCode;
    
    ProductCategory(String displayName, boolean isMainCategory, String productCode) {
        this.displayName = displayName;
        this.isMainCategory = isMainCategory;
        this.productCode = productCode;
    }
    
    /**
     * Gets the display name for the category
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if this is a main category (shows as green button)
     * @return true if main category, false if subcategory
     */
    public boolean isMainCategory() {
        return isMainCategory;
    }
    
    /**
     * Gets the product code for this category
     * @return Product code or null for main categories
     */
    public String getProductCode() {
        return productCode;
    }
    
    /**
     * Gets all main categories
     * @return Array of main categories
     */
    public static ProductCategory[] getMainCategories() {
        return new ProductCategory[] {
            FRIGIDERE,
            VITRINE_FRIG,
            DULAPI_SPUMATI,
            AC,
            DOZATOARE,
            CALORIFERE_ELECTRICE
        };
    }
    
    /**
     * Gets subcategories for a given main category
     * @param mainCategory The main category
     * @return Array of subcategories
     */
    public static ProductCategory[] getSubCategories(ProductCategory mainCategory) {
        switch (mainCategory) {
            case FRIGIDERE:
                return new ProductCategory[] {
                    ECHIPAMENTE_FRIGORIFICE,
                    ECHIPAMENTE_FRIGORIFICE_FARA_MOTOR,
                    FRIGIDERE_AMONIAC
                };
            case VITRINE_FRIG:
                return new ProductCategory[] {
                    VITRINE_FRIGORIFICE,
                    VITRINE_FRIGORIFICE_FARA_MOTOR,
                    ALTE_APARATE_MARI_REFRIGERARE
                };
            case DULAPI_SPUMATI:
                return new ProductCategory[] {
                    DULAPI_SPUMATI_FS_390,
                    DULAPI_SPUMATI_CF,
                    USI_SPUMATE_FS_CF
                };
            case AC:
                return new ProductCategory[] {
                    ECHIPAMENTE_AER_CONDITIONAT,
                    ECHIPAMENTE_AER_CONDITIONAT_CTG_B
                };
            case DOZATOARE:
                return new ProductCategory[] {
                    DISTRIBUITOARE_AGENT_FRIGORIFIC
                };
            case CALORIFERE_ELECTRICE:
                return new ProductCategory[] {
                    ALTE_ECHIPAMENTE_TRANSFER_TERMIC,
                    APARATE_ELECTRICE_INCALZIT,
                    APARATE_ELECTRICE_INCALZIT_BOILERE
                };
            default:
                return new ProductCategory[0];
        }
    }
    
    /**
     * Gets the main category for a subcategory
     * @param subCategory The subcategory
     * @return The main category or null if not found
     */
    public static ProductCategory getMainCategoryFor(ProductCategory subCategory) {
        for (ProductCategory mainCategory : getMainCategories()) {
            ProductCategory[] subCategories = getSubCategories(mainCategory);
            for (ProductCategory sub : subCategories) {
                if (sub == subCategory) {
                    return mainCategory;
                }
            }
        }
        return null;
    }
    
    /**
     * Finds a category by its product code
     * @param productCode The product code to search for
     * @return The matching category or null if not found
     */
    public static ProductCategory findByProductCode(String productCode) {
        if (productCode == null) {
            return null;
        }
        
        for (ProductCategory category : values()) {
            if (productCode.equals(category.getProductCode())) {
                return category;
            }
        }
        return null;
    }
    
    /**
     * Finds a category by its display name
     * @param displayName The display name to search for
     * @return The matching category or null if not found
     */
    public static ProductCategory findByDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        
        for (ProductCategory category : values()) {
            if (displayName.equals(category.getDisplayName())) {
                return category;
            }
        }
        return null;
    }
}