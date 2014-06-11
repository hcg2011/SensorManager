/* **************************************************
 Copyright (c) 2012, University of Cambridge
 Neal Lathia, neal.lathia@cl.cam.ac.uk
 Kiran Rachuri, kiran.rachuri@cl.cam.ac.uk

This library was developed as part of the EPSRC Ubhave (Ubiquitous and
Social Computing for Positive Behaviour Change) Project. For more
information, please visit http://www.emotionsense.org

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ************************************************** */

package com.ubhave.sensormanager.sensors.pull;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.ubhave.sensormanager.config.sensors.pull.MotionSensorConfig;

public abstract class AbstractMotionSensor extends AbstractPullSensor
{	
	private final int motionSensorType;
	private final SensorEventListener listener; // data listener
	private final SensorManager sensorManager; // Controls the hardware sensor
	
	protected ArrayList<float[]> sensorReadings;
	protected ArrayList<Long> sensorReadingTimestamps;

	protected AbstractMotionSensor(final Context context, final int motionSensorType)
	{
		super(context);
		this.motionSensorType = motionSensorType;
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		listener = new SensorEventListener()
		{

			// This method is required by the API and is called when the
			// accuracy of the
			// readings being generated by the accelerometer changes.
			// We don't do anything when this happens.
			public void onAccuracyChanged(Sensor sensor, int accuracy)
			{
			}

			// This method is called when the accelerometer takes a reading:
			// despite the name, it is called whether even if it's the same as
			// the previous one
			public void onSensorChanged(SensorEvent event)
			{
				try
				{
					if (isSensing)
					{
						synchronized (sensorReadings)
						{
							if (isSensing)
							{
								float[] data = new float[3];

								for (int i = 0; i < 3; i++)
								{
									data[i] = event.values[i];
								}

								sensorReadings.add(data);
								sensorReadingTimestamps.add(System.currentTimeMillis());
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
	}

	protected boolean startSensing()
	{
		sensorReadings = new ArrayList<float[]>();
		sensorReadingTimestamps = new ArrayList<Long>();

		int sensorDelay = (Integer) sensorConfig.getParameter(MotionSensorConfig.SAMPLING_DELAY);
		boolean registrationSuccess = sensorManager.registerListener(listener, sensorManager.getDefaultSensor(motionSensorType), sensorDelay);
		return registrationSuccess;
	}

	protected void stopSensing()
	{
		sensorManager.unregisterListener(listener);
	}
}