import dbus
import urllib

from log import log

def _byte_to_string(char):
    if 32 <= char < 127:
        return "%c" % char
    else:
        return urllib.quote(chr(char))

def _byte_array_to_string(bs):
    return ''.join([_byte_to_string(char) for char in bs])

AIRBEARS_SSID = "AirBears"

def has_airbears():
    networks = get_connected_wireless()
    log("Connected to networks: %s" % ','.join(networks))
    return AIRBEARS_SSID in networks

def get_connected_wireless():
    ssids = []

    bus = dbus.SystemBus()

    nm_proxy = bus.get_object("org.freedesktop.NetworkManager", "/org/freedesktop/NetworkManager")
    nm_iface = dbus.Interface(nm_proxy, "org.freedesktop.NetworkManager")
    nm_props = dbus.Interface(nm_proxy, "org.freedesktop.DBus.Properties")

    active_cons = nm_props.Get("org.freedesktop.NetworkManager", "ActiveConnections")
    for active in active_cons:
        active_proxy = bus.get_object("org.freedesktop.NetworkManager", active)
        active_props = dbus.Interface(active_proxy, "org.freedesktop.DBus.Properties")

        con_path = active_props.Get("org.freedesktop.NetworkManager.Connection.Active", "Connection")
        con_proxy = bus.get_object("org.freedesktop.NetworkManager", con_path)

        connection = dbus.Interface(con_proxy, "org.freedesktop.NetworkManager.Settings.Connection")
        settings = connection.GetSettings()
        if '802-11-wireless' in settings:
            ssids.append(_byte_array_to_string(settings['802-11-wireless']['ssid']))
    return ssids

if __name__=="__main__":
    from pprint import pprint
    pprint("Connected wireless: %s" % ",".join(get_connected_wireless()))
    pprint("Has AirBears: %s" % str(has_airbears()))
