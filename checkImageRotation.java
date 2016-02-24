
public static File checkImageRotation(Context context, Uri imageUrl) {
		File files = null;
		int angle = 0;
		boolean hasRotation = false;

    // Use content resolver to check the image orientation
		try {
			String[] projection = { Images.ImageColumns.ORIENTATION };
			Cursor cursor = context.getContentResolver().query(imageUrl, projection, null, null, null);
			if (cursor.moveToFirst()) {
				angle = cursor.getInt(0);
				hasRotation = true;
			}
			cursor.close();
		} catch (Exception e) {
		  e.printStackTrace();
		  
			hasRotation = false;
		}

    // If there have no info or get problem, then use the basic function to check 
		try {
			String path = imageUrl.getPath();
			File originalImageFile = new File(path);

			if (!hasRotation) {
				ExifInterface exif = new ExifInterface(path);
				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

				if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
					angle = 90;
				} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
					angle = 180;
				} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
					angle = 270;
				}
			}

			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			
			// output file
			OutputStream imagefile = new FileOutputStream(######);
			
			Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUrl);
			
			Bitmap correctBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), mat, true);
			correctBitmap.compress(CompressFormat.PNG, 100, imagefile);
			
			// delete if no need
			f.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return files;
	}
