/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhketech.sip.app.project.client.linphone;

public class Utility {

    private static String username = "7009";//yyt
    private static String password = "1231456";
    private static String host = "192.168.0.60";//sip.linphone.org

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String musername) {
        username = musername;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String mpassword) {
        password = mpassword;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String mhost) {
        host = mhost;
    }
}