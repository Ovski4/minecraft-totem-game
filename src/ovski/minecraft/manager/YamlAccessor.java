package ovski.minecraft.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
 
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * YamlAccessor
 * 
 * Allow to access a Yaml file, then configure it or get data from it.
 * 
 * This class comes from https://gist.github.com/SagaciousZed/3174347
 * It has been updated so it does not depend of a specific plugin anymore
 * More info at http://wiki.bukkit.org/Configuration_API_Reference#Implementation_for_Reloading
 * 
 * @author baptiste <baptiste.bouchereau@gmail.com>
 */
public class YamlAccessor
{
    private final File file;
    private File configFile;
    private FileConfiguration fileConfiguration;

    /**
     * Constructor
     * 
     * @param f : a File
     */
    public YamlAccessor(File f)
    {
        if (!f.exists()) {
            throw new IllegalArgumentException("The file "+f.getAbsolutePath()+" must exist");
        }
        this.file = f;
        this.configFile = f;
    }

    /**
     * Reload the file configuration
     * Load it at first, then reload it if the content of the file changed
     * 
     * @throws FileNotFoundException
     */
    public void reloadConfig() throws FileNotFoundException
    {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        // Look for defaults in the jar
        InputStream defConfigStream = new FileInputStream(file);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    /**
     * Get the config of the file
     * 
     * @return fileConfiguration
     */
    public FileConfiguration getConfig()
    {
        if (fileConfiguration == null) {
            try {
                this.reloadConfig();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return fileConfiguration;
    }

    /**
     * Save the config
     */
    public void saveConfig()
    {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}