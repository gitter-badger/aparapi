/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.syncleus.aparapi.internal.opencl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.syncleus.aparapi.Config;
import com.syncleus.aparapi.internal.jni.OpenCLJNI;

/**
 * This class is intended to be a singleton which determines if OpenCL is available upon startup of Aparapi
 */
public class OpenCLLoader extends OpenCLJNI{

   private static final Logger logger = Logger.getLogger(Config.getLoggerName());

   private static boolean openCLAvailable = false;

   private static final OpenCLLoader instance = new OpenCLLoader();

   static {
      if (Config.useAgent) {
         logger.fine("Using agent!");
         openCLAvailable = true;
      } else {
         final String arch = System.getProperty("os.arch");
         logger.fine("arch = " + arch);
         String aparapiLibraryName = null;

         if (arch.equals("amd64") || arch.equals("x86_64")) {
            aparapiLibraryName = "aparapi_x86_64";
         } else if (arch.equals("x86") || arch.equals("i386")) {
            aparapiLibraryName = "aparapi_x86";
         } else {
            logger.warning("Expected property os.arch to contain amd64, x86_64, x86 or i386 but instead found " + arch
                  + " as a result we don't know which aparapi to attempt to load.");
         }
         if (aparapiLibraryName != null) {
            logger.fine("attempting to load aparapi shared lib " + aparapiLibraryName);

            try {
               Runtime.getRuntime().loadLibrary(aparapiLibraryName);
               openCLAvailable = true;
            } catch (final UnsatisfiedLinkError e) {
               logger.log(Level.SEVERE, "Check your environment. Failed to load aparapi native library " + aparapiLibraryName
                     + " or possibly failed to locate opencl native library (opencl.dll/opencl.so)."
                     + " Ensure that both are in your PATH (windows) or in LD_LIBRARY_PATH (linux).");
            }
         }
      }
   }

   /**
    * Retrieve a singleton instance of OpenCLLoader
    * 
    * @return A singleton instance of OpenCLLoader
    */
   protected static OpenCLLoader getInstance() {
      return instance;
   }

   /**
    * Retrieve the status of whether OpenCL was successfully loaded
    * 
    * @return The status of whether OpenCL was successfully loaded
    */
   public static boolean isOpenCLAvailable() {
      return openCLAvailable;
   }
}
