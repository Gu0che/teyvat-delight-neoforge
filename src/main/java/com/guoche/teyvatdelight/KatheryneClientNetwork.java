package com.guoche.teyvatdelight;

/** @deprecated Use {@link com.guoche.teyvatdelight.client.katheryne.KatheryneClientNetwork} for new integrations. */
@Deprecated
public final class KatheryneClientNetwork {
    private KatheryneClientNetwork() {
    }

    public static void receive(KatheryneSnapshot snapshot) {
        com.guoche.teyvatdelight.client.katheryne.KatheryneClientNetwork.receive(snapshot);
    }
}
