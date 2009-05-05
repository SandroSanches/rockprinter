/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ufba.dp.j3d;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Locale;
import javax.media.j3d.VirtualUniverse;

/**
 *
 * @author Rafael
 */
class InnerModel
  {
    VirtualUniverse universe;
    Locale          locale;
    BranchGroup     scene;

    public InnerModel ()
    {
      universe = new SimpleUniverse ();
      locale   = new Locale(universe);
    }

    public void setScene (BranchGroup scene)
    {
      if (this.scene != null)
        locale.removeBranchGraph (this.scene);

      this.scene = scene;
      locale.addBranchGraph (this.scene);
    }

    public void addViewingPlatform (ViewingPlatform viewingPlatform)
    {
      locale.addBranchGraph (viewingPlatform);
    }
  }
