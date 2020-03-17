import os
import struct
import numpy as np
import gzip

def load_mnist(path, kind='train'):
    labels_path = os.path.join(path, 
                               '%s-labels-idx1-ubyte.gz' % kind)
    images_path = os.path.join(path, 
                               '%s-images-idx3-ubyte.gz' % kind)
        
    with gzip.open(labels_path, 'rb') as lbpath:
        lbpath.read(8)
        buffer = lbpath.read()
        labels = np.frombuffer(buffer, dtype=np.uint8)

    with gzip.open(images_path, 'rb') as imgpath:
        imgpath.read(16)
        buffer = imgpath.read()
        images = np.frombuffer(buffer, 
                               dtype=np.uint8).reshape(
            len(labels), 784)
 
    return images, labels

def show_image(image_data):
    import numpy as np
    import matplotlib.pyplot as plt
    pixels = np.array(image_data.reshape(28, 28), dtype='float')
    plt.imshow(pixels, cmap='gray')
    plt.show()