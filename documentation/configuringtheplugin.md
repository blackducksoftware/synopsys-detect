# Configuring the Synopsys Detect Plugin
Use the following process to configure the Synopsys Detect for Jenkins plugin.  Note that the supported credentials formats are user name and password or API token.  SAML is not supported.

1. After installing, navigate to **Manage Jenkins** > **Configure System**.
1. Navigate to the **Synopsys Detect** section, and complete the following.
   1. **Global download strategy**: Select an option to **Install AirGapped Detect as a Tool Installation** or **Download via scripts or use DETECT\_JAR**

![](../images/Configuring1.png)![](../images/Configuring2.png)

1. **Black Duck URL**: Your Black Duck server instance.
1. **Black Duck credentials**: To add credentials, click **Add** > **Jenkins**, and then select the type of credentials that you want to add and populate the relevant fields.
   When you add credentials, you can select those credentials that you want from the drop-down menu to authenticate to the Black Duck server. 
   1. For user API tokens, select **Secret text** from the menu in the **Kind** field, then provide your Black Duck access token in the **Secret** field.
   1. The other option for credentials is **Username with password**.

![](../images/Configuring3.png)

1. The **Advanced...** option displays for Black Duck.  Advanced settings enable you to specify values for:
   1. **Black Duck connection timeout** (in seconds).  The default value is 120.
   1. **Trust Black Duck certificates**: Select the checkbox to allow Secure Socket Layers (SSL) certificates from Black Duck.

![](../images/Configuring4.jpeg)

1. Click **Test Connection to Black Duck** to verify that your settings are correct. If so, a *Connection successful!* status displays.
1. Click **Save**.

The Polaris fields in the plugin were removed in version 3.0.0. 
The Polaris functionality has moved to [Synopsys Polaris for Jenkins](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/475922472/Synopsys+Polaris+for+Jenkins+Integration).