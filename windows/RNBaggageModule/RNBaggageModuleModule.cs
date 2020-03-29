using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Baggage.Module.RNBaggageModule
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNBaggageModuleModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNBaggageModuleModule"/>.
        /// </summary>
        internal RNBaggageModuleModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNBaggageModule";
            }
        }
    }
}
