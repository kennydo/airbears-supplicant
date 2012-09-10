import sys

if sys.platform == "win32":
    from wlan_win32 import *
elif sys.platform == "darwin":
    from wlan_darwin import *
elif sys.platform.startswith('linux'):
    from wlan_linux import *
