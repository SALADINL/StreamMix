["java:package:com.intel.libgdxmisslecommand.android.streammix"]
module App {
    interface Stream {
        string addMusic(string music);
        string removeMusic();
        string playMusic();
        string pauseMusic();
    };
};