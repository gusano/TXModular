// amended for TX Modular 2007

//CyclOSC
// a grid of cells sent via OSC
// Steve Symons
// http://muio.org
// June 2006

// based on 

// WebCam
// by REAS <http://reas.com>

// Reading and displaying an image from an
// attached Capture device.

// Updated 8 April 2005

//july 2006 - added fullscreen mode using 'fullscreen_api" see file for details 

import oscP5.*;
import netP5.*;
import processing.video.*;

Capture camera;


// oscP5 instance for the osc communication
OscP5 oscP5;
NetAddress myRemoteLocation;

int receiveAtPort;
int sendToPort;
String host;
String oscP5event;

void setup()
{
//  size(640, 480);  
  size(320, 240);
  //for (int y = 0; y < 256; y++){lastImage[y] = 0;}
  // List all available capture devices to the console
  // Use the information printed to the text area to
  // correctly set the variable "s" below
  println(Capture.list());

  // Specify your own device by the name of the capture
  // device returned from the Capture.list() function
  String s = "IIDC FireWire Video"; 
  //String s = "Logitech QuickCam Zoom-WDM";
  //
  camera = new Capture(this, 320, 240, 10);

  // If no device is specified, will just use the default
  //camera = new Capture(this, 320, 240, 12);

  // Opens the settings page for this capture device
  //camera.settings();
  frameRate(10);

  // change resolution to 640, 480
//  setResolution( 640, 480 );

  // let ctrl+f switch to window mode
//  createFullScreenKeyBindings();
  //start in full screen mode
//  setFullScreen( true );
  initOsc();
}

void initOsc() {
  receiveAtPort = 12000;
  sendToPort = 57120;
  host = "127.0.0.1";
  oscP5event = "oscEvent";
  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this,12000);
//  oscP5 = new OscP5( this, host, sendToPort, receiveAtPort, oscP5event  );
//  oscP5 = new OscP5( this, host, sendToPort);
  myRemoteLocation = new NetAddress(host, sendToPort);
}

void oscEvent(OscMessage theOscMessage){
    // comments
}

void captureEvent(Capture camera)
{
  camera.read();

}

void mousePressed() 
{ 
  camera.settings();
  //int c = averageImage(camera.get(mouseX-8,mouseY-8,16,16));
  //fill(c);
  //rect(mouseX,mouseY,16,16);
}

// averages an image
int averageImage(PImage im){
  float av = 0;
  for (int y = 0; y < im.pixels.length; y++){
    av += (red(im.pixels[y])+green(im.pixels[y])+blue(im.pixels[y]))/3;
  }
  av = av/im.pixels.length;
  return int(av);
}


void draw()
{
  Object[] tObj;

  // draws the camera image
  //image(camera,320,40, 320,241);

  //sets the grid up
  int numX = 16;
  int numY = 16;

  // tObj is for OSC
  tObj = new Object[numX + 1];

  // the size of the block in pixels that gets averaged
  int blockSizeX = 320/numX;
  int blockSizeY = 240/numY;
  int displayBlockX = width/numX;
  int displayBlockY = height/numY;

  // c = averaged colour
  int c;
  // loop down the rows
  for(int j=0; j<numY; j++) {
    // add the row number to the osc message
    tObj[0] = new Integer(j);
    // loop the cols
    for(int i=0; i<numX; i++) {
      //grab a block of the camera image and average it
      c = averageImage(camera.get((i*blockSizeX)-blockSizeX/2,(j*blockSizeY)-blockSizeY/2, blockSizeX, blockSizeY));

      //set colours for drawing and draw block
 //     fill(c);
 //     stroke(c);
//      rect((i*displayBlockX),(j*displayBlockY), displayBlockX, displayBlockY);

      // add the colour to the osc message
      tObj[i + 1] = new Integer(c);
    }
    // send the row as osc
    OscMessage myMessage = new OscMessage("/txcyclosc/grayrow");
    myMessage.add(tObj); 
    /* send the message */
    oscP5.send(myMessage, myRemoteLocation); 
//  println(myMessage);  } 
}
}
