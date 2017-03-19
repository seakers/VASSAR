/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author Ana-Dani
 */
public class Orbit {
    private String type;
    private String altitude;
    private String inclination;
    private String eccentricity;
    private String semimajor_axis;
    private String raan;
    private String arg_perigee;
    private String mean_anomaly;
    private String nplanes;
    private String num_sats_per_plane;
    private String mission_arch;
    public Orbit () {
        
    }
    public Orbit(String orb, int np, int nsat) {
        String[] tokens = orb.split("-");
        type = tokens[0];
        altitude = tokens[1];
        inclination = tokens[2];
        raan = tokens[3];
        nplanes = String.valueOf(np);
        num_sats_per_plane = String.valueOf(nsat);
        mission_arch = "single_arch";
        eccentricity = "0.0";
    }
    public Orbit(String orb) {
        String[] tokens = orb.split("-");
        type = tokens[0];
        altitude = tokens[1];
        inclination = tokens[2];
        raan = tokens[3];
        nplanes = "1";
        num_sats_per_plane = "1";
        mission_arch = "single_arch";
        eccentricity = "0.0";
    }
    public Orbit(String t, String a, String i, String ra) {
        type = t;
        altitude = a;
        inclination = i;
        raan = ra;
        nplanes = "1";
        mission_arch = "single_arch";
        eccentricity = "0.0";
    }
    public String getAltitude() {
        return altitude;
    }

    public String getArg_perigee() {
        return arg_perigee;
    }

    public String getInclination() {
        return inclination;
    }

    public String getEccentricity() {
        return eccentricity;
    }

    public String getMean_anomaly() {
        return mean_anomaly;
    }

    public String getRaan() {
        return raan;
    }

    public String getSemimajor_axis() {
        return semimajor_axis;
    }

    public String getType() {
        return type;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public void setArg_perigee(String arg_perigee) {
        this.arg_perigee = arg_perigee;
    }

    public void setEccentricity(String eccentricity) {
        this.eccentricity = eccentricity;
    }

    public void setInclination(String inclination) {
        this.inclination = inclination;
    }

    public void setMean_anomaly(String mean_anomaly) {
        this.mean_anomaly = mean_anomaly;
    }

    public void setRaan(String raan) {
        this.raan = raan;
    }

    public void setSemimajor_axis(String semimajor_axis) {
        this.semimajor_axis = semimajor_axis;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "-" + altitude + "-" + inclination + "-" + raan;
    }
    public String toJessSlots() {
        return " (num-of-planes# " + nplanes + ")" +
            " (num-of-sats-per-plane# "  + num_sats_per_plane + ")"  + 
            " (mission-architecture " + mission_arch + ") " +     
            " (orbit-type " + type + ")"  + 
            " (orbit-altitude# "  + altitude + ")"  + 
            " (orbit-eccentricity "  + eccentricity + ")"  + 
            " (orbit-RAAN " + raan + ")"  + 
            " (orbit-inclination " + inclination + ")"  + 
            " (orbit-string " + this.toString() + ")";

    }

    public String getMission_arch() {
        return mission_arch;
    }

    public void setMission_arch(String mission_arch) {
        this.mission_arch = mission_arch;
    }

    public String getNplanes() {
        return nplanes;
    }

    public void setNplanes(String nplanes) {
        this.nplanes = nplanes;
    }

    public String getNum_sats_per_plane() {
        return num_sats_per_plane;
    }

    public void setNum_sats_per_plane(String num_sats_per_plane) {
        this.num_sats_per_plane = num_sats_per_plane;
    }
    
}
