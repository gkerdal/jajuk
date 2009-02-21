/*
 *  Jajuk
 *  Copyright (C) 2003 The Jajuk Team
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  $Revision: 2523 $
 */
package org.jajuk.services.players;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jajuk.base.File;
import org.jajuk.services.webradio.WebRadio;
import org.jajuk.util.Conf;
import org.jajuk.util.Const;
import org.jajuk.util.UtilString;
import org.jajuk.util.UtilSystem;
import org.jajuk.util.log.Log;

/**
 * Mplayer player implementation
 */
public abstract class AbstractMPlayerImpl implements IPlayerImpl, Const {

  /** Stored Volume */
  float fVolume;

  /** Mplayer process */
  volatile Process proc;

  /** End of file flag * */
  volatile boolean bEOF = false;

  /** File is opened flag * */
  volatile boolean bOpening = false;

  /** Stop position thread flag */
  volatile boolean bStop = false;

  /** Fading state */
  volatile boolean bFading = false;

  /** pause flag * */
  protected volatile boolean bPaused = false;

  /*
   * 
   * Kill abruptly the mplayer process (this way, killing is synchronous, and
   * easier than sending a quit command). Do not try to send a 'quit' command to
   * mplayer because then, it's not possible to differentiate end of file from
   * forced quit and the fifo will comes out of control
   */
  public void stop() throws Exception {
    bFading = false;
    this.bStop = true;
    Log.debug("Stop");
    if (proc != null) {
      if (UtilSystem.isUnderLinux()) {
        /*
         * Under linux (not sure if it may happen on others Unix and never
         * reproduced under Windows), mplayer process can "zombified" after
         * destroy() method call for unknown reason (linked with the mplayer
         * slave mode ?). Even worse, these processes block the dsp audio line
         * and then all new mplayer processes fail. To avoid this, we force a
         * kill -9 on every process after a destoy() call under Linux.
         */
        Field field = proc.getClass().getDeclaredField("pid");
        field.setAccessible(true);
        int pid = field.getInt(proc);
        proc.destroy();
        try {
          ProcessBuilder pb = new ProcessBuilder("kill", "-9", Integer.toString(pid));
          pb.start();
        } catch (Error error) {
          Log.error(error);
        }
      } else {
        proc.destroy();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.IPlayerImpl#setVolume(float)
   */
  public void setVolume(float fVolume) {
    this.fVolume = fVolume;
    sendCommand("volume " + (int) (100 * fVolume) + " 2");
    // Not not log this when fading, generates too mush logs
    if (!bFading) {
      Log.debug("Set Volume= " + (int) (100 * fVolume) + " %");
    }
  }

  /**
   * Send a command to mplayer slave
   * 
   * @param command
   */
  protected void sendCommand(String command) {
    if (proc != null) {
      PrintStream out = new PrintStream(proc.getOutputStream());

      // Do not use println() : it doesn't work under windows
      out.print(command + '\n');
      out.flush();

      // don't close out here otherwise the output stream of the Process
      // will be closed as well and subsequent sendCommant() calls will silently
      // fail!!
    }
  }

  /**
   * @return current volume as a float ex: 0.2f
   */
  public float getCurrentVolume() {
    return fVolume;
  }

  /**
   * Build the mplayer command line
   * 
   * @param url
   *          to play
   * @return command line as a String array
   */
  List<String> buildCommand(String url) {
    String sCommand = "mplayer";
    // Use any forced mplayer path
    String forced = Conf.getString(Const.CONF_MPLAYER_PATH_FORCED);
    if (!UtilString.isVoid(forced)) {
      sCommand = forced;
    } else {
      if (UtilSystem.isUnderWindows()) {
        sCommand = UtilSystem.getMPlayerWindowsPath().getAbsolutePath();
      } else if (UtilSystem.isUnderOSXintel() || UtilSystem.isUnderOSXpower()) {
        sCommand = UtilSystem.getMPlayerOSXPath();
      }
    }
    String sAdditionalArgs = Conf.getString(Const.CONF_MPLAYER_ARGS);
    // Build command
    List<String> cmd = new ArrayList<String>(10);
    cmd.add(sCommand);
    // quiet: less traces
    cmd.add("-quiet");
    // slave: slave mode (control with stdin)
    cmd.add("-slave");
    // -af volume: Use volnorm to limit gain to max
    // If mute, use -200db otherwise, use a linear scale
    cmd.add("-af");
    cmd.add("volume=" + ((fVolume == 0) ? -200 : ((int) (25 * fVolume) - 20)));
    // -softvol : use soft mixer, allows to set volume only to this mplayer
    // instance, not others programs
    cmd.add("-softvol");
    // Define a cache. It is useful to avoid sound gliches but also to
    // overide a local mplayer large cache configuration in
    // ~/.mplayer/config file. User can set a large cache for video for ie.
    String cacheSize = "500";
    // 500Kb, mplayer starts before the cache is filled up
    if (!Conf.getString(Const.CONF_MPLAYER_ARGS).matches(".*-cache.*")) {
      // If user already forced a cache value, do not overwrite it
      cmd.add("-cache");
      cmd.add(cacheSize);
    }
    if (!UtilString.isVoid(sAdditionalArgs)) {
      // Add any additional arguments provided by user
      String[] sArgs = sAdditionalArgs.split(" ");
      for (String element : sArgs) {
        cmd.add(element);
      }
    }
    // If it is a playlist, add the -playlist option, must be the last option
    // because options after -playlist are ignored (see mplayer man page)
    if (url.matches("http://.*")) {
      cmd.add("-playlist");
    }
    cmd.add(url);
    return cmd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.players.IPlayerImpl#getCurrentLength()
   */
  public long getCurrentLength() {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.players.IPlayerImpl#getCurrentPosition()
   */
  public float getCurrentPosition() {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.players.IPlayerImpl#getElapsedTime()
   */
  public long getElapsedTime() {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.players.IPlayerImpl#play(org.jajuk.base.File, float, long,
   *      float)
   */
  public abstract void play(File file, float fPosition, long length, float fVolume)
      throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.players.IPlayerImpl#play(org.jajuk.base.WebRadio, float)
   */
  public abstract void play(WebRadio radio, float fVolume) throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.players.IPlayerImpl#seek(float)
   */
  public void seek(float fPosition) {
    // TODO Auto-generated method stub

  }

  /**
   * @return player state, -1 if player is null.
   */
  public int getState() {
    return -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.players.IPlayerImpl#pause()
   */
  public void pause() throws Exception {
    bPaused = true;
    sendCommand("pause");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.players.IPlayerImpl#resume()
   */
  public void resume() throws Exception {
    bPaused = false;
    sendCommand("pause");
  }

}