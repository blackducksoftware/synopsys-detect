{
  "Name": "Nuget Inspector Inspection Result",
  "Version": "1.0.0",
  "Containers": [
    {
      "Name": "dwCheckApi",
      "Type": "Solution",
      "SourcePath": "${sourcePath?replace("\\", "/")}/dwCheckApi.sln",
      "OutputPaths": [],
      "Packages": [],
      "Dependencies": [],
      "Children": [
        {
          "Name": "dwCheckApi",
          "Version": "2.0.0.0",
          "Type": "Project",
          "SourcePath": "${sourcePath?replace("\\", "/")}/dwCheckApi/dwCheckApi.csproj",
          "OutputPaths": [
            "${sourcePath?replace("\\", "/")}/dwCheckApi/bin/Debug/netcoreapp2.0/",
            "${sourcePath?replace("\\", "/")}/dwCheckApi/bin/Release/netcoreapp2.0/"
          ],
          "Packages": [
            {
              "PackageId": {
                "Name": "ClacksMiddlware",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.All",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Libuv",
                "Version": "1.10.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.ApplicationInsights",
                "Version": "2.4.0"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Diagnostics.StackTrace",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.ApplicationInsights.AspNetCore",
                "Version": "2.1.1"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.ApplicationInsights",
                  "Version": "2.4.0"
                },
                {
                  "Name": "Microsoft.ApplicationInsights.DependencyCollector",
                  "Version": "2.4.1"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DiagnosticAdapter",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Net.NameResolution",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.ApplicationInsights.DependencyCollector",
                "Version": "2.4.1"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.ApplicationInsights",
                  "Version": "2.4.0"
                },
                {
                  "Name": "Microsoft.Extensions.PlatformAbstractions",
                  "Version": "1.1.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Diagnostics.StackTrace",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.IISIntegration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Https",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.CommandLine",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.UserSecrets",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Console",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Debug",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.All",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Antiforgery",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ApplicationInsights.HostingStartup",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Cookies",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Facebook",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Google",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.JwtBearer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.MicrosoftAccount",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OpenIdConnect",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Twitter",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authorization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authorization.Policy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.AzureAppServices.HostingStartup",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.AzureAppServicesIntegration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.CookiePolicy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Cors",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.Internal",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.KeyDerivation",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection.AzureStorage",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Server.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Html.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Features",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.HttpOverrides",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Identity",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Identity.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.JsonPatch",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Localization.Routing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.MiddlewareAnalysis",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ApiExplorer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Cors",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.DataAnnotations",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Formatters.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Formatters.Xml",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor.ViewCompilation",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.RazorPages",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.TagHelpers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.NodeServices",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Owin",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Language",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Runtime",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCaching",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCaching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCompression",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Rewrite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.HttpSys",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.IISIntegration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Https",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Libuv",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Session",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.SpaServices",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.StaticFiles",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebSockets",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Data.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Data.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.InMemory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.SqlServer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Tools",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Redis",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.SqlServer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.AzureKeyVault",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Binder",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.CommandLine",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Ini",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.UserSecrets",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Xml",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DiagnosticAdapter",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Composite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Embedded",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileSystemGlobbing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Identity.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Identity.Stores",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.AzureAppServices",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Console",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Debug",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.EventSource",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.TraceSource",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.ObjectPool",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options.ConfigurationExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.WebEncoders",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.VisualStudio.Web.BrowserLink",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Antiforgery",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.ObjectPool",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.ApplicationInsights.HostingStartup",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.ApplicationInsights.AspNetCore",
                  "Version": "2.1.1"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Runtime",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DiagnosticAdapter",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Configuration",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.WebEncoders",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Cookies",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Facebook",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Google",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.JwtBearer",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.IdentityModel.Protocols.OpenIdConnect",
                  "Version": "2.1.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.MicrosoftAccount",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.OpenIdConnect",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.IdentityModel.Protocols.OpenIdConnect",
                  "Version": "2.1.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Twitter",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authorization",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authorization.Policy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authorization",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.AzureAppServices.HostingStartup",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.AzureAppServicesIntegration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.AzureAppServicesIntegration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.AzureAppServices",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.CookiePolicy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Cors",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Cryptography.Internal",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Cryptography.KeyDerivation",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.Internal",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.DataProtection",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.Internal",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Win32.Registry",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Security.Cryptography.Xml",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.DataProtection.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.DataProtection.AzureStorage",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "WindowsAzure.Storage",
                  "Version": "8.1.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.DataProtection.Extensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Diagnostics",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Diagnostics.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Diagnostics.EntityFrameworkCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Hosting",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Server.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Server.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Hosting.Server.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Features",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Html.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Http",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.ObjectPool",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Http.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Features",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Http.Extensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Buffers",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Http.Features",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.HttpOverrides",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Identity",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Cookies",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.KeyDerivation",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Identity.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Identity.EntityFrameworkCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Identity",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Identity.Stores",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.JsonPatch",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Localization",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Localization.Routing",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.MiddlewareAnalysis",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ApiExplorer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Cors",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.DataAnnotations",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Formatters.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.RazorPages",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.TagHelpers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.ApiExplorer",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authorization.Policy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCaching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyModel",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Cors",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Cors",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.DataAnnotations",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Formatters.Json",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.JsonPatch",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Formatters.Xml",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Localization",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Razor",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Runtime",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.CSharp",
                  "Version": "2.3.1"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Composite",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Razor.Extensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Razor.Language",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.Razor",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Razor.ViewCompilation",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.RazorPages",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.RazorPages",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.TagHelpers",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Runtime",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileSystemGlobbing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Antiforgery",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Html.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.DataAnnotations",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Formatters.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.WebEncoders",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json.Bson",
                  "Version": "1.0.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.NodeServices",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Console",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Owin",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Razor",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Razor.Language",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Razor.Runtime",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Html.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.ResponseCaching",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCaching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.ResponseCaching.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.ResponseCompression",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Rewrite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Routing",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.ObjectPool",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.HttpSys",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Win32.Registry",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.IISIntegration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.HttpOverrides",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Libuv",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Threading.Tasks.Extensions",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel.Https",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Features",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Buffers",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Numerics.Vectors",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Libuv",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Libuv",
                  "Version": "1.10.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Session",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.SpaServices",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.TagHelpers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.NodeServices",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.StaticFiles",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.WebEncoders",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.WebSockets",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Numerics.Vectors",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.WebUtilities",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Azure.KeyVault",
                "Version": "2.3.2"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Azure.KeyVault.WebKey",
                  "Version": "2.0.7"
                },
                {
                  "Name": "Microsoft.Rest.ClientRuntime",
                  "Version": "2.3.8"
                },
                {
                  "Name": "Microsoft.Rest.ClientRuntime.Azure",
                  "Version": "3.3.7"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Net.Http",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Azure.KeyVault.WebKey",
                "Version": "2.0.7"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.CodeAnalysis.Analyzers",
                "Version": "1.1.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.CodeAnalysis.Common",
                "Version": "2.3.1"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CodeAnalysis.Analyzers",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.AppContext",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Immutable",
                  "Version": "1.4.0"
                },
                {
                  "Name": "System.Console",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.FileVersionInfo",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.StackTrace",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Dynamic.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.Compression",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Numerics",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.CodePages",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks.Parallel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Thread",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ValueTuple",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XPath.XDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.CodeAnalysis.CSharp",
                "Version": "2.3.1"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CodeAnalysis.Common",
                  "Version": "2.3.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.CodeAnalysis.Razor",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Razor.Language",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.CSharp",
                  "Version": "2.3.1"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.Common",
                  "Version": "2.3.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.CSharp",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.Edm",
                "Version": "5.8.2"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.OData",
                "Version": "5.8.2"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.Edm",
                  "Version": "5.8.2"
                },
                {
                  "Name": "System.Spatial",
                  "Version": "5.8.2"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.Sqlite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.bundle_green",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.DotNet.PlatformAbstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.AppContext",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices.RuntimeInformation",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Remotion.Linq",
                  "Version": "2.1.1"
                },
                {
                  "Name": "System.Collections.Immutable",
                  "Version": "1.4.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Interactive.Async",
                  "Version": "3.1.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Design",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.InMemory",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.bundle_green",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.SqlServer",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Data.SqlClient",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Tools",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Memory",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Redis",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "StackExchange.Redis.StrongName",
                  "Version": "1.2.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.SqlServer",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Data.SqlClient",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.AzureKeyVault",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Azure.KeyVault",
                  "Version": "2.3.2"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.IdentityModel.Clients.ActiveDirectory",
                  "Version": "3.14.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Binder",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.CommandLine",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Ini",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Json",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.UserSecrets",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Xml",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Security.Cryptography.Xml",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyModel",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.DotNet.PlatformAbstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Dynamic.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DiagnosticAdapter",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Composite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Embedded",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Physical",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileSystemGlobbing",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileSystemGlobbing",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Hosting.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Identity.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.KeyDerivation",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Identity.Stores",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Identity.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Localization",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Localization.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.AzureAppServices",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ValueTuple",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Configuration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options.ConfigurationExtensions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Console",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Debug",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.EventSource",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.TraceSource",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.ObjectPool",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Options",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Options.ConfigurationExtensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Binder",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.PlatformAbstractions",
                "Version": "1.1.0"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Primitives",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime.CompilerServices.Unsafe",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.WebEncoders",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Clients.ActiveDirectory",
                "Version": "3.14.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Json",
                  "Version": "4.0.2"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Logging",
                "Version": "1.1.4"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Protocols",
                "Version": "2.1.4"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections.Specialized",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Contracts",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IdentityModel.Tokens.Jwt",
                  "Version": "5.1.4"
                },
                {
                  "Name": "System.Net.Http",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Protocols.OpenIdConnect",
                "Version": "2.1.4"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.IdentityModel.Protocols",
                  "Version": "2.1.4"
                },
                {
                  "Name": "System.Dynamic.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Tokens",
                "Version": "5.1.4"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.IdentityModel.Logging",
                  "Version": "1.1.4"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices.RuntimeInformation",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Claims",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Net.Http.Headers",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Buffers",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.App",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostPolicy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetAppHost",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostPolicy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostResolver",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostResolver",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetAppHost",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Platforms",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Targets",
                "Version": "1.1.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Rest.ClientRuntime",
                "Version": "2.3.8"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Rest.ClientRuntime.Azure",
                "Version": "3.3.7"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Rest.ClientRuntime",
                  "Version": "2.3.8"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.VisualStudio.Web.BrowserLink",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Win32.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Win32.Registry",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Security.AccessControl",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "NETStandard.Library",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Newtonsoft.Json",
                "Version": "10.0.3"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.TypeConverter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Formatters",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Newtonsoft.Json.Bson",
                "Version": "1.0.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Remotion.Linq",
                "Version": "2.1.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Queryable",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.debian.8-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.fedora.23-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.fedora.24-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.native.System",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Data.SqlClient.sni",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "runtime.win-arm64.runtime.native.System.Data.SqlClient.sni",
                  "Version": "4.4.0"
                },
                {
                  "Name": "runtime.win-x64.runtime.native.System.Data.SqlClient.sni",
                  "Version": "4.4.0"
                },
                {
                  "Name": "runtime.win-x86.runtime.native.System.Data.SqlClient.sni",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.IO.Compression",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Net.Http",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Net.Security",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Security.Cryptography.Apple",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "runtime.osx.10.10-x64.runtime.native.System.Security.Cryptography.Apple",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "runtime.debian.8-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.fedora.23-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.fedora.24-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.opensuse.13.2-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.opensuse.42.1-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.osx.10.10-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.rhel.7-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.ubuntu.14.04-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.ubuntu.16.04-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.ubuntu.16.10-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.opensuse.13.2-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.opensuse.42.1-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.osx.10.10-x64.runtime.native.System.Security.Cryptography.Apple",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.osx.10.10-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.rhel.7-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.ubuntu.14.04-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.ubuntu.16.04-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.ubuntu.16.10-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.win-arm64.runtime.native.System.Data.SqlClient.sni",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.win-x64.runtime.native.System.Data.SqlClient.sni",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.win-x86.runtime.native.System.Data.SqlClient.sni",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.bundle_green",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.core",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "StackExchange.Redis.StrongName",
                "Version": "1.2.4"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.Compression",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.NameResolution",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Security",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Sockets",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices.RuntimeInformation",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Thread",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.ThreadPool",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Timer",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.AppContext",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Buffers",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Collections",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Concurrent",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Immutable",
                "Version": "1.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Collections.NonGeneric",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Specialized",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Annotations",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.TypeConverter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Specialized",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Console",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Data.SqlClient",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Win32.Registry",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Text.Encoding.CodePages",
                  "Version": "4.4.0"
                },
                {
                  "Name": "runtime.native.System.Data.SqlClient.sni",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Contracts",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Debug",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.DiagnosticSource",
                "Version": "4.4.1"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.FileVersionInfo",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.StackTrace",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Tools",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Tracing",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Dynamic.Runtime",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Globalization",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Globalization.Calendars",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Globalization.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IdentityModel.Tokens.Jwt",
                "Version": "5.1.4"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.IdentityModel.Tokens",
                  "Version": "5.1.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Interactive.Async",
                "Version": "3.1.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.Compression",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Buffers",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.IO.Compression",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Expressions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Queryable",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.Http",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Net.Http",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.NameResolution",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.Security",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Win32.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Claims",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Principal",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.ThreadPool",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Net.Security",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.Sockets",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Numerics.Vectors",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.ObjectModel",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Private.DataContractSerialization",
                "Version": "4.1.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlSerializer",
                  "Version": "4.0.11"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.ILGeneration",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.Lightweight",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Metadata",
                "Version": "1.5.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.TypeExtensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Resources.ResourceManager",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.CompilerServices.Unsafe",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Handles",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.InteropServices",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.InteropServices.RuntimeInformation",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Numerics",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Formatters",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Json",
                "Version": "4.0.2"
              },
              "Dependencies": [
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Private.DataContractSerialization",
                  "Version": "4.1.1"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.AccessControl",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Claims",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Principal",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Algorithms",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Numerics",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.Apple",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Cng",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Csp",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Encoding",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Numerics",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.X509Certificates",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Calendars",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Numerics",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Cng",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Csp",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Net.Http",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Xml",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Security.Principal",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Principal.Windows",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Spatial",
                "Version": "5.8.2"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding.CodePages",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encodings.Web",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Text.RegularExpressions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks.Extensions",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks.Parallel",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Thread",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.ThreadPool",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Timer",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ValueTuple",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Xml.ReaderWriter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks.Extensions",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XDocument",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XmlDocument",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XmlSerializer",
                "Version": "4.0.11"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XPath",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XPath.XDocument",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XPath",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "WindowsAzure.Storage",
                "Version": "8.1.4"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.OData",
                  "Version": "5.8.2"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Spatial",
                  "Version": "5.8.2"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Common",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Tools",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.DAL",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "dwCheckApi.Entities",
                  "Version": "1.0.0"
                },
                {
                  "Name": "dwCheckApi.Persistence",
                  "Version": "1.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.DTO",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "dwCheckApi.Entities",
                  "Version": "1.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Entities",
                "Version": "1.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Persistence",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Tools",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "dwCheckApi.Common",
                  "Version": "1.0.0"
                },
                {
                  "Name": "dwCheckApi.Entities",
                  "Version": "1.0.0"
                }
              ]
            }
          ],
          "Dependencies": [
            {
              "Name": "Microsoft.NETCore.App",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.AspNetCore.All",
              "Version": "2.0.0"
            },
            {
              "Name": "NETStandard.Library",
              "Version": "2.0.0"
            },
            {
              "Name": "ClacksMiddlware",
              "Version": "2.0.0"
            }
          ],
          "Children": []
        },
        {
          "Name": "dwCheckApi.DAL",
          "Version": "1.0.0.0",
          "Type": "Project",
          "SourcePath": "${sourcePath?replace("\\", "/")}/dwCheckApi.DAL/dwCheckApi.DAL.csproj",
          "OutputPaths": [
            "${sourcePath?replace("\\", "/")}/dwCheckApi.DAL/bin/Debug/netcoreapp2.0/",
            "${sourcePath?replace("\\", "/")}/dwCheckApi.DAL/bin/Release/netcoreapp2.0/"
          ],
          "Packages": [
            {
              "PackageId": {
                "Name": "Microsoft.CSharp",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Remotion.Linq",
                  "Version": "2.1.1"
                },
                {
                  "Name": "System.Collections.Immutable",
                  "Version": "1.4.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Interactive.Async",
                  "Version": "3.1.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Design",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.bundle_green",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Tools",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Memory",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Json",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Physical",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileSystemGlobbing",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileSystemGlobbing",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Options",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Primitives",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime.CompilerServices.Unsafe",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.App",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostPolicy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetAppHost",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostPolicy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostResolver",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostResolver",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetAppHost",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Platforms",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Targets",
                "Version": "1.1.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "NETStandard.Library",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Newtonsoft.Json",
                "Version": "10.0.3"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.TypeConverter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Formatters",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Remotion.Linq",
                "Version": "2.1.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.1.0"
                },
                {
                  "Name": "System.Linq.Queryable",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.0.12"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.bundle_green",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.core",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Immutable",
                "Version": "1.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Collections.NonGeneric",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Specialized",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Annotations",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.TypeConverter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Specialized",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Debug",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.DiagnosticSource",
                "Version": "4.4.1"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Globalization",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Globalization.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Interactive.Async",
                "Version": "3.1.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Expressions",
                "Version": "4.1.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.0.12"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Queryable",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ObjectModel",
                "Version": "4.0.12"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.ILGeneration",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.Lightweight",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.TypeExtensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Resources.ResourceManager",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.CompilerServices.Unsafe",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Handles",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.InteropServices",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Formatters",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.RegularExpressions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.ReaderWriter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XmlDocument",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Common",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Tools",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Entities",
                "Version": "1.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Persistence",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Tools",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "dwCheckApi.Common",
                  "Version": "1.0.0"
                },
                {
                  "Name": "dwCheckApi.Entities",
                  "Version": "1.0.0"
                }
              ]
            }
          ],
          "Dependencies": [
            {
              "Name": "Microsoft.NETCore.App",
              "Version": "2.0.0"
            }
          ],
          "Children": []
        },
        {
          "Name": "dwCheckApi.DTO",
          "Version": "1.0.0.0",
          "Type": "Project",
          "SourcePath": "${sourcePath?replace("\\", "/")}/dwCheckApi.DTO/dwCheckApi.DTO.csproj",
          "OutputPaths": [
            "${sourcePath?replace("\\", "/")}/dwCheckApi.DTO/bin/Debug/netcoreapp2.0/",
            "${sourcePath?replace("\\", "/")}/dwCheckApi.DTO/bin/Release/netcoreapp2.0/"
          ],
          "Packages": [
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.App",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostPolicy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetAppHost",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostPolicy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostResolver",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostResolver",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetAppHost",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Platforms",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "NETStandard.Library",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Entities",
                "Version": "1.0.0"
              },
              "Dependencies": []
            }
          ],
          "Dependencies": [
            {
              "Name": "Microsoft.NETCore.App",
              "Version": "2.0.0"
            }
          ],
          "Children": []
        },
        {
          "Name": "dwCheckApi.Entities",
          "Version": "1.0.0.0",
          "Type": "Project",
          "SourcePath": "${sourcePath?replace("\\", "/")}/dwCheckApi.Entities/dwCheckApi.Entities.csproj",
          "OutputPaths": [
            "${sourcePath?replace("\\", "/")}/dwCheckApi.Entities/bin/Debug/netcoreapp2.0/",
            "${sourcePath?replace("\\", "/")}/dwCheckApi.Entities/bin/Release/netcoreapp2.0/"
          ],
          "Packages": [
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.App",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostPolicy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetAppHost",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostPolicy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostResolver",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostResolver",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetAppHost",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Platforms",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "NETStandard.Library",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            }
          ],
          "Dependencies": [
            {
              "Name": "Microsoft.NETCore.App",
              "Version": "2.0.0"
            }
          ],
          "Children": []
        },
        {
          "Name": "dwCheckApi.Persistence",
          "Version": "1.0.0.0",
          "Type": "Project",
          "SourcePath": "${sourcePath?replace("\\", "/")}/dwCheckApi.Persistence/dwCheckApi.Persistence.csproj",
          "OutputPaths": [
            "${sourcePath?replace("\\", "/")}/dwCheckApi.Persistence/bin/Debug/netcoreapp2.0/",
            "${sourcePath?replace("\\", "/")}/dwCheckApi.Persistence/bin/Release/netcoreapp2.0/"
          ],
          "Packages": [
            {
              "PackageId": {
                "Name": "Microsoft.CSharp",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Remotion.Linq",
                  "Version": "2.1.1"
                },
                {
                  "Name": "System.Collections.Immutable",
                  "Version": "1.4.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Interactive.Async",
                  "Version": "3.1.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Design",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.bundle_green",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Tools",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Memory",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Json",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Physical",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileSystemGlobbing",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileSystemGlobbing",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Options",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Primitives",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime.CompilerServices.Unsafe",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.App",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostPolicy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetAppHost",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostPolicy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostResolver",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostResolver",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetAppHost",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Platforms",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Targets",
                "Version": "1.1.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "NETStandard.Library",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Newtonsoft.Json",
                "Version": "10.0.3"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.TypeConverter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Formatters",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Remotion.Linq",
                "Version": "2.1.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.1.0"
                },
                {
                  "Name": "System.Linq.Queryable",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.0.12"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.bundle_green",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.core",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Immutable",
                "Version": "1.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Collections.NonGeneric",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Specialized",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Annotations",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.TypeConverter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Specialized",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Debug",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.DiagnosticSource",
                "Version": "4.4.1"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Globalization",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Globalization.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Interactive.Async",
                "Version": "3.1.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Expressions",
                "Version": "4.1.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.0.12"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Queryable",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ObjectModel",
                "Version": "4.0.12"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.ILGeneration",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.Lightweight",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.TypeExtensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Resources.ResourceManager",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.CompilerServices.Unsafe",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Handles",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.InteropServices",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Formatters",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.RegularExpressions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.ReaderWriter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XmlDocument",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Common",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Tools",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Entities",
                "Version": "1.0.0"
              },
              "Dependencies": []
            }
          ],
          "Dependencies": [
            {
              "Name": "Microsoft.NETCore.App",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore.Design",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore.Sqlite",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
              "Version": "2.0.0-preview1-final"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore.Tools",
              "Version": "2.0.0"
            },
            {
              "Name": "NETStandard.Library",
              "Version": "2.0.0"
            },
            {
              "Name": "Newtonsoft.Json",
              "Version": "10.0.3"
            }
          ],
          "Children": []
        },
        {
          "Name": "dwCheckApi.Tests",
          "Version": "2.0.0.0",
          "Type": "Project",
          "SourcePath": "${sourcePath?replace("\\", "/")}/dwCheckApi.Tests/dwCheckApi.Tests.csproj",
          "OutputPaths": [
            "${sourcePath?replace("\\", "/")}/dwCheckApi.Tests/bin/Debug/netcoreapp2.0/",
            "${sourcePath?replace("\\", "/")}/dwCheckApi.Tests/bin/Release/netcoreapp2.0/"
          ],
          "Packages": [
            {
              "PackageId": {
                "Name": "Castle.Core",
                "Version": "4.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.AppContext",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Specialized",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel.TypeConverter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Console",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.TraceSource",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Dynamic.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlSerializer",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "ClacksMiddlware",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.All",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Libuv",
                "Version": "1.10.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.ApplicationInsights",
                "Version": "2.4.0"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Diagnostics.StackTrace",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.ApplicationInsights.AspNetCore",
                "Version": "2.1.1"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.ApplicationInsights",
                  "Version": "2.4.0"
                },
                {
                  "Name": "Microsoft.ApplicationInsights.DependencyCollector",
                  "Version": "2.4.1"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DiagnosticAdapter",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Net.NameResolution",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.ApplicationInsights.DependencyCollector",
                "Version": "2.4.1"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.ApplicationInsights",
                  "Version": "2.4.0"
                },
                {
                  "Name": "Microsoft.Extensions.PlatformAbstractions",
                  "Version": "1.1.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Diagnostics.StackTrace",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.IISIntegration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Https",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.CommandLine",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.UserSecrets",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Console",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Debug",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.All",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Antiforgery",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ApplicationInsights.HostingStartup",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Cookies",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Facebook",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Google",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.JwtBearer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.MicrosoftAccount",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OpenIdConnect",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Twitter",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authorization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authorization.Policy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.AzureAppServices.HostingStartup",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.AzureAppServicesIntegration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.CookiePolicy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Cors",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.Internal",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.KeyDerivation",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection.AzureStorage",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Server.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Html.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Features",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.HttpOverrides",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Identity",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Identity.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.JsonPatch",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Localization.Routing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.MiddlewareAnalysis",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ApiExplorer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Cors",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.DataAnnotations",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Formatters.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Formatters.Xml",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor.ViewCompilation",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.RazorPages",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.TagHelpers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.NodeServices",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Owin",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Language",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Runtime",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCaching",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCaching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCompression",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Rewrite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.HttpSys",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.IISIntegration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Https",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Libuv",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Session",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.SpaServices",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.StaticFiles",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebSockets",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Data.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Data.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.InMemory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.SqlServer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Tools",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Redis",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.SqlServer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.AzureKeyVault",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Binder",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.CommandLine",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Ini",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.UserSecrets",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Xml",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DiagnosticAdapter",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Composite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Embedded",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileSystemGlobbing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Identity.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Identity.Stores",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.AzureAppServices",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Console",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Debug",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.EventSource",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.TraceSource",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.ObjectPool",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options.ConfigurationExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.WebEncoders",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.VisualStudio.Web.BrowserLink",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Antiforgery",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.ObjectPool",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.ApplicationInsights.HostingStartup",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.ApplicationInsights.AspNetCore",
                  "Version": "2.1.1"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Runtime",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DiagnosticAdapter",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Configuration",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.WebEncoders",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Cookies",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Facebook",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Google",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.JwtBearer",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.IdentityModel.Protocols.OpenIdConnect",
                  "Version": "2.1.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.MicrosoftAccount",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.OpenIdConnect",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.IdentityModel.Protocols.OpenIdConnect",
                  "Version": "2.1.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authentication.Twitter",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.OAuth",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authorization",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Authorization.Policy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authorization",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.AzureAppServices.HostingStartup",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.AzureAppServicesIntegration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.AzureAppServicesIntegration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.AzureAppServices",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.CookiePolicy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Cors",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Cryptography.Internal",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Cryptography.KeyDerivation",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.Internal",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.DataProtection",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.Internal",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.DataProtection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Win32.Registry",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Security.Cryptography.Xml",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.DataProtection.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.DataProtection.AzureStorage",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "WindowsAzure.Storage",
                  "Version": "8.1.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.DataProtection.Extensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Diagnostics",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Diagnostics.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Diagnostics.EntityFrameworkCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Hosting",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Server.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Server.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Hosting.Server.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Features",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Html.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Http",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.ObjectPool",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Http.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Features",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Http.Extensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Buffers",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Http.Features",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.HttpOverrides",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Identity",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Cookies",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.KeyDerivation",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Identity.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Identity.EntityFrameworkCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Identity",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Identity.Stores",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.JsonPatch",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Localization",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Localization.Routing",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.MiddlewareAnalysis",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ApiExplorer",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Cors",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.DataAnnotations",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Formatters.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.RazorPages",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.TagHelpers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.ApiExplorer",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Authorization.Policy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCaching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyModel",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Cors",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Cors",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.DataAnnotations",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Formatters.Json",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.JsonPatch",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Formatters.Xml",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Localization",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Localization",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Razor",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Runtime",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.CSharp",
                  "Version": "2.3.1"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Composite",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Razor.Extensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Razor.Language",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.Razor",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.Razor.ViewCompilation",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.RazorPages",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.RazorPages",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.TagHelpers",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Razor",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor.Runtime",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileSystemGlobbing",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Antiforgery",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Diagnostics.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Html.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.DataAnnotations",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.Formatters.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.WebEncoders",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json.Bson",
                  "Version": "1.0.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.NodeServices",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Console",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Owin",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Razor",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Razor.Language",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Razor.Runtime",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Html.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Razor",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.ResponseCaching",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.ResponseCaching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.ResponseCaching.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.ResponseCompression",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Rewrite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Routing",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.ObjectPool",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Routing.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.HttpSys",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Win32.Registry",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.IISIntegration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Authentication.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.HttpOverrides",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Libuv",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.WebUtilities",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Threading.Tasks.Extensions",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel.Https",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Core",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Features",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Buffers",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Numerics.Vectors",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Libuv",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Libuv",
                  "Version": "1.10.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Server.Kestrel.Transport.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.Session",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.DataProtection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.SpaServices",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Mvc.TagHelpers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Mvc.ViewFeatures",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.NodeServices",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.StaticFiles",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.WebEncoders",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.WebSockets",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Numerics.Vectors",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.AspNetCore.WebUtilities",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Net.Http.Headers",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Azure.KeyVault",
                "Version": "2.3.2"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Azure.KeyVault.WebKey",
                  "Version": "2.0.7"
                },
                {
                  "Name": "Microsoft.Rest.ClientRuntime",
                  "Version": "2.3.8"
                },
                {
                  "Name": "Microsoft.Rest.ClientRuntime.Azure",
                  "Version": "3.3.7"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Net.Http",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Azure.KeyVault.WebKey",
                "Version": "2.0.7"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.CodeAnalysis.Analyzers",
                "Version": "1.1.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.CodeAnalysis.Common",
                "Version": "2.3.1"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CodeAnalysis.Analyzers",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.AppContext",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Immutable",
                  "Version": "1.4.0"
                },
                {
                  "Name": "System.Console",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.FileVersionInfo",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.StackTrace",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Dynamic.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.Compression",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Numerics",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.CodePages",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks.Parallel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Thread",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ValueTuple",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XPath.XDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.CodeAnalysis.CSharp",
                "Version": "2.3.1"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CodeAnalysis.Common",
                  "Version": "2.3.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.CodeAnalysis.Razor",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Razor.Language",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.CSharp",
                  "Version": "2.3.1"
                },
                {
                  "Name": "Microsoft.CodeAnalysis.Common",
                  "Version": "2.3.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.CSharp",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.Edm",
                "Version": "5.8.2"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.OData",
                "Version": "5.8.2"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.Edm",
                  "Version": "5.8.2"
                },
                {
                  "Name": "System.Spatial",
                  "Version": "5.8.2"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.Sqlite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.bundle_green",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.DotNet.PlatformAbstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.AppContext",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices.RuntimeInformation",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Remotion.Linq",
                  "Version": "2.1.1"
                },
                {
                  "Name": "System.Collections.Immutable",
                  "Version": "1.4.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Interactive.Async",
                  "Version": "3.1.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Design",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.InMemory",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.bundle_green",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.SqlServer",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Data.SqlClient",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Tools",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Memory",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Redis",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "StackExchange.Redis.StrongName",
                  "Version": "1.2.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.SqlServer",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Data.SqlClient",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.AzureKeyVault",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Azure.KeyVault",
                  "Version": "2.3.2"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.IdentityModel.Clients.ActiveDirectory",
                  "Version": "3.14.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Binder",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.CommandLine",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Ini",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Json",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.UserSecrets",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Xml",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Security.Cryptography.Xml",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyModel",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.DotNet.PlatformAbstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Dynamic.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DiagnosticAdapter",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Composite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Embedded",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Physical",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileSystemGlobbing",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileSystemGlobbing",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Hosting.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Identity.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Cryptography.KeyDerivation",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Identity.Stores",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Identity.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Localization",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Localization.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Localization.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.AzureAppServices",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.EnvironmentVariables",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ValueTuple",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Configuration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options.ConfigurationExtensions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Console",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Debug",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.EventSource",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.TraceSource",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.ObjectPool",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Options",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Options.ConfigurationExtensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Binder",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.PlatformAbstractions",
                "Version": "1.1.0"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Primitives",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime.CompilerServices.Unsafe",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.WebEncoders",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Text.Encodings.Web",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Clients.ActiveDirectory",
                "Version": "3.14.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Json",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Logging",
                "Version": "1.1.4"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Protocols",
                "Version": "2.1.4"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections.Specialized",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Contracts",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IdentityModel.Tokens.Jwt",
                  "Version": "5.1.4"
                },
                {
                  "Name": "System.Net.Http",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Protocols.OpenIdConnect",
                "Version": "2.1.4"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.IdentityModel.Protocols",
                  "Version": "2.1.4"
                },
                {
                  "Name": "System.Dynamic.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.IdentityModel.Tokens",
                "Version": "5.1.4"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.IdentityModel.Logging",
                  "Version": "1.1.4"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices.RuntimeInformation",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Claims",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Net.Http.Headers",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Buffers",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NET.Test.Sdk",
                "Version": "15.3.0-preview-20170628-02"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.TestPlatform.TestHost",
                  "Version": "15.3.0-preview-20170628-02"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.App",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostPolicy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetAppHost",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostPolicy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostResolver",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostResolver",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetAppHost",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Platforms",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Targets",
                "Version": "1.1.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Rest.ClientRuntime",
                "Version": "2.3.8"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Rest.ClientRuntime.Azure",
                "Version": "3.3.7"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Rest.ClientRuntime",
                  "Version": "2.3.8"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.TestPlatform.ObjectModel",
                "Version": "15.3.0-preview-20170628-02"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.EventBasedAsync",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel.TypeConverter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Process",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.TextWriterTraceListener",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.TraceSource",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices.RuntimeInformation",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Loader",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Json",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Thread",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XPath.XmlDocument",
                  "Version": "4.0.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.TestPlatform.TestHost",
                "Version": "15.3.0-preview-20170628-02"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyModel",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.TestPlatform.ObjectModel",
                  "Version": "15.3.0-preview-20170628-02"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.VisualStudio.Web.BrowserLink",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.AspNetCore.Hosting.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.Http.Extensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Win32.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Win32.Registry",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Security.AccessControl",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Moq",
                "Version": "4.7.25"
              },
              "Dependencies": [
                {
                  "Name": "Castle.Core",
                  "Version": "4.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Queryable",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "NETStandard.Library",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Newtonsoft.Json",
                "Version": "10.0.3"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.TypeConverter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Formatters",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Newtonsoft.Json.Bson",
                "Version": "1.0.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Remotion.Linq",
                "Version": "2.1.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Queryable",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.debian.8-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.fedora.23-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.fedora.24-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.native.System",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Data.SqlClient.sni",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "runtime.win-arm64.runtime.native.System.Data.SqlClient.sni",
                  "Version": "4.4.0"
                },
                {
                  "Name": "runtime.win-x64.runtime.native.System.Data.SqlClient.sni",
                  "Version": "4.4.0"
                },
                {
                  "Name": "runtime.win-x86.runtime.native.System.Data.SqlClient.sni",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.IO.Compression",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Net.Http",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Net.Security",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Security.Cryptography.Apple",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "runtime.osx.10.10-x64.runtime.native.System.Security.Cryptography.Apple",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "runtime.debian.8-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.fedora.23-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.fedora.24-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.opensuse.13.2-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.opensuse.42.1-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.osx.10.10-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.rhel.7-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.ubuntu.14.04-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.ubuntu.16.04-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.ubuntu.16.10-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "runtime.opensuse.13.2-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.opensuse.42.1-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.osx.10.10-x64.runtime.native.System.Security.Cryptography.Apple",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.osx.10.10-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.rhel.7-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.ubuntu.14.04-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.ubuntu.16.04-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.ubuntu.16.10-x64.runtime.native.System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.win-arm64.runtime.native.System.Data.SqlClient.sni",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.win-x64.runtime.native.System.Data.SqlClient.sni",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "runtime.win-x86.runtime.native.System.Data.SqlClient.sni",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.bundle_green",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.core",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "StackExchange.Redis.StrongName",
                "Version": "1.2.4"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.Compression",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.NameResolution",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Security",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Sockets",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices.RuntimeInformation",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Thread",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.ThreadPool",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Timer",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.AppContext",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Buffers",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Collections",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Concurrent",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Immutable",
                "Version": "1.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Collections.NonGeneric",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Specialized",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Annotations",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.EventBasedAsync",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.TypeConverter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Specialized",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Console",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Data.SqlClient",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Win32.Registry",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Text.Encoding.CodePages",
                  "Version": "4.4.0"
                },
                {
                  "Name": "runtime.native.System.Data.SqlClient.sni",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Contracts",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Debug",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.DiagnosticSource",
                "Version": "4.4.1"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.FileVersionInfo",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Process",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Win32.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "Microsoft.Win32.Registry",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Thread",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.ThreadPool",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.StackTrace",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Metadata",
                  "Version": "1.5.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.TextWriterTraceListener",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.TraceSource",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Tools",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.TraceSource",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Tracing",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Dynamic.Runtime",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Globalization",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Globalization.Calendars",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Globalization.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IdentityModel.Tokens.Jwt",
                "Version": "5.1.4"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.IdentityModel.Tokens",
                  "Version": "5.1.4"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Interactive.Async",
                "Version": "3.1.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.Compression",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Buffers",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.IO.Compression",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Expressions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Queryable",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.Http",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Net.Http",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.NameResolution",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.Security",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Win32.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Claims",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.X509Certificates",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Principal",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.ThreadPool",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Net.Security",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Net.Sockets",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Net.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Numerics.Vectors",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.ObjectModel",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Private.DataContractSerialization",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlSerializer",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.ILGeneration",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.Lightweight",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Metadata",
                "Version": "1.5.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.TypeExtensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Resources.ResourceManager",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.CompilerServices.Unsafe",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Handles",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.InteropServices",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.InteropServices.RuntimeInformation",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Loader",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Numerics",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Formatters",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Json",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Private.DataContractSerialization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.AccessControl",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Security.Principal.Windows",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Claims",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Principal",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Algorithms",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Numerics",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.Apple",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Cng",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Csp",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Encoding",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.OpenSsl",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Numerics",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.X509Certificates",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Calendars",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Numerics",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Algorithms",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Cng",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Csp",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Security.Cryptography.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Net.Http",
                  "Version": "4.3.0"
                },
                {
                  "Name": "runtime.native.System.Security.Cryptography.OpenSsl",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Cryptography.Xml",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Security.Principal",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Security.Principal.Windows",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Spatial",
                "Version": "5.8.2"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding.CodePages",
                "Version": "4.4.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encodings.Web",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Text.RegularExpressions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks.Extensions",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks.Parallel",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections.Concurrent",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tracing",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Thread",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.ThreadPool",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Timer",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ValueTuple",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Xml.ReaderWriter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks.Extensions",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XDocument",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Tools",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XmlDocument",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XmlSerializer",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XPath",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XPath.XDocument",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XDocument",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XPath",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XPath.XmlDocument",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XPath",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "WindowsAzure.Storage",
                "Version": "8.1.4"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.OData",
                  "Version": "5.8.2"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "System.Spatial",
                  "Version": "5.8.2"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "xunit",
                "Version": "2.3.0"
              },
              "Dependencies": [
                {
                  "Name": "xunit.analyzers",
                  "Version": "0.7.0"
                },
                {
                  "Name": "xunit.assert",
                  "Version": "2.3.0"
                },
                {
                  "Name": "xunit.core",
                  "Version": "2.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "xunit.abstractions",
                "Version": "2.0.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "xunit.analyzers",
                "Version": "0.7.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "xunit.assert",
                "Version": "2.3.0"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "xunit.core",
                "Version": "2.3.0"
              },
              "Dependencies": [
                {
                  "Name": "xunit.extensibility.core",
                  "Version": "2.3.0"
                },
                {
                  "Name": "xunit.extensibility.execution",
                  "Version": "2.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "xunit.extensibility.core",
                "Version": "2.3.0"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "xunit.abstractions",
                  "Version": "2.0.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "xunit.extensibility.execution",
                "Version": "2.3.0"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "xunit.extensibility.core",
                  "Version": "2.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "ClacksMiddlware",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.AspNetCore.All",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.App",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "dwCheckApi.DAL",
                  "Version": "1.0.0"
                },
                {
                  "Name": "dwCheckApi.DTO",
                  "Version": "1.0.0"
                },
                {
                  "Name": "dwCheckApi.Entities",
                  "Version": "1.0.0"
                },
                {
                  "Name": "dwCheckApi.Persistence",
                  "Version": "1.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Common",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Tools",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Json",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.DAL",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "dwCheckApi.Entities",
                  "Version": "1.0.0"
                },
                {
                  "Name": "dwCheckApi.Persistence",
                  "Version": "1.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.DTO",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "dwCheckApi.Entities",
                  "Version": "1.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Entities",
                "Version": "1.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "dwCheckApi.Persistence",
                "Version": "1.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Tools",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                },
                {
                  "Name": "dwCheckApi.Common",
                  "Version": "1.0.0"
                },
                {
                  "Name": "dwCheckApi.Entities",
                  "Version": "1.0.0"
                }
              ]
            }
          ],
          "Dependencies": [
            {
              "Name": "Microsoft.NETCore.App",
              "Version": "2.0.0"
            },
            {
              "Name": "xunit",
              "Version": "2.3.0"
            },
            {
              "Name": "Moq",
              "Version": "4.7.25"
            },
            {
              "Name": "Microsoft.NET.Test.Sdk",
              "Version": "15.3.0-preview-20170628-02"
            }
          ],
          "Children": []
        },
        {
          "Name": "dwCheckApi.Common",
          "Version": "1.0.0.0",
          "Type": "Project",
          "SourcePath": "${sourcePath?replace("\\", "/")}/dwCheckApi.Common/dwCheckApi.Common.csproj",
          "OutputPaths": [
            "${sourcePath?replace("\\", "/")}/dwCheckApi.Common/bin/Debug/netcoreapp2.0/",
            "${sourcePath?replace("\\", "/")}/dwCheckApi.Common/bin/Release/netcoreapp2.0/"
          ],
          "Packages": [
            {
              "PackageId": {
                "Name": "Microsoft.CSharp",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Data.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Memory",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Remotion.Linq",
                  "Version": "2.1.1"
                },
                {
                  "Name": "System.Collections.Immutable",
                  "Version": "1.4.0"
                },
                {
                  "Name": "System.ComponentModel.Annotations",
                  "Version": "4.4.0"
                },
                {
                  "Name": "System.Diagnostics.DiagnosticSource",
                  "Version": "4.4.1"
                },
                {
                  "Name": "System.Interactive.Async",
                  "Version": "3.1.1"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Design",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.bundle_green",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Data.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
                "Version": "2.0.0-preview1-final"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Relational.Design",
                  "Version": "2.0.0-preview1-final"
                },
                {
                  "Name": "Microsoft.EntityFrameworkCore.Sqlite.Core",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.EntityFrameworkCore.Tools",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.EntityFrameworkCore.Design",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Caching.Memory",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Caching.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileProviders.Physical",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Configuration.Json",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Configuration",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Configuration.FileExtensions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Newtonsoft.Json",
                  "Version": "10.0.3"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileProviders.Physical",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.FileProviders.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.FileSystemGlobbing",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.FileSystemGlobbing",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Logging.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Options",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Logging.Abstractions",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Options",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.Extensions.DependencyInjection.Abstractions",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.Extensions.Primitives",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.Extensions.Primitives",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime.CompilerServices.Unsafe",
                  "Version": "4.4.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.App",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostPolicy",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetAppHost",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostPolicy",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetHostResolver",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.DotNetHostResolver",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.DotNetAppHost",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Platforms",
                "Version": "2.0.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "Microsoft.NETCore.Targets",
                "Version": "1.1.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "NETStandard.Library",
                "Version": "2.0.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Newtonsoft.Json",
                "Version": "10.0.3"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.CSharp",
                  "Version": "4.4.0"
                },
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.ComponentModel.TypeConverter",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Formatters",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.XmlDocument",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "Remotion.Linq",
                "Version": "2.1.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.1.0"
                },
                {
                  "Name": "System.Linq.Queryable",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.0.12"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.bundle_green",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                  "Version": "1.1.7"
                },
                {
                  "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.core",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.linux",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.osx",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.lib.e_sqlite3.v110_xp",
                "Version": "1.1.7"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "SQLitePCLRaw.provider.e_sqlite3.netstandard11",
                "Version": "1.1.7"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                },
                {
                  "Name": "SQLitePCLRaw.core",
                  "Version": "1.1.7"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Immutable",
                "Version": "1.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Collections.NonGeneric",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Collections.Specialized",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Annotations",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ComponentModel.TypeConverter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.NonGeneric",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Collections.Specialized",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ComponentModel.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.Debug",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Diagnostics.DiagnosticSource",
                "Version": "4.4.1"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Globalization",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Globalization.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Interactive.Async",
                "Version": "3.1.1"
              },
              "Dependencies": [
                {
                  "Name": "NETStandard.Library",
                  "Version": "2.0.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.IO.FileSystem.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Expressions",
                "Version": "4.1.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.ObjectModel",
                  "Version": "4.0.12"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Emit.Lightweight",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.TypeExtensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Linq.Queryable",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Linq.Expressions",
                  "Version": "4.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.ObjectModel",
                "Version": "4.0.12"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.ILGeneration",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Emit.Lightweight",
                "Version": "4.0.1"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Emit.ILGeneration",
                  "Version": "4.0.1"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Reflection.TypeExtensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Resources.ResourceManager",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.CompilerServices.Unsafe",
                "Version": "4.4.0"
              },
              "Dependencies": []
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Handles",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.InteropServices",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Handles",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Formatters",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Reflection",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Serialization.Primitives",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Runtime.Serialization.Primitives",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.Encoding.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Text.RegularExpressions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "Microsoft.NETCore.Platforms",
                  "Version": "2.0.0"
                },
                {
                  "Name": "Microsoft.NETCore.Targets",
                  "Version": "1.1.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Threading.Tasks.Extensions",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.ReaderWriter",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO.FileSystem.Primitives",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.InteropServices",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.RegularExpressions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading.Tasks.Extensions",
                  "Version": "4.3.0"
                }
              ]
            },
            {
              "PackageId": {
                "Name": "System.Xml.XmlDocument",
                "Version": "4.3.0"
              },
              "Dependencies": [
                {
                  "Name": "System.Collections",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Diagnostics.Debug",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Globalization",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.IO",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Resources.ResourceManager",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Runtime.Extensions",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Text.Encoding",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Threading",
                  "Version": "4.3.0"
                },
                {
                  "Name": "System.Xml.ReaderWriter",
                  "Version": "4.3.0"
                }
              ]
            }
          ],
          "Dependencies": [
            {
              "Name": "Microsoft.NETCore.App",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore.Design",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore.Sqlite",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore.Sqlite.Design",
              "Version": "2.0.0-preview1-final"
            },
            {
              "Name": "Microsoft.EntityFrameworkCore.Tools",
              "Version": "2.0.0"
            },
            {
              "Name": "Newtonsoft.Json",
              "Version": "10.0.3"
            },
            {
              "Name": "NETStandard.Library",
              "Version": "2.0.0"
            },
            {
              "Name": "Microsoft.Extensions.Configuration.Json",
              "Version": "2.0.0"
            }
          ],
          "Children": []
        }
      ]
    }
  ]
}