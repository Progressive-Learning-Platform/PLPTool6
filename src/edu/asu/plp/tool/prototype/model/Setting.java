package edu.asu.plp.tool.prototype.model;

/**
 * Any Class or Enum can extend this interface to be used as a key for retrieving a setting from
 * {@link edu.asu.plp.tool.prototype.ApplicationSettings}
 *
 * The value passed into {@link edu.asu.plp.tool.prototype.ApplicationSettings#getSetting(Setting)} will have its
 * respective toString() method called.
 *
 * @author Nesbitt, Morgan
 * Created on 2/23/2016.
 */
public interface Setting
{
}
