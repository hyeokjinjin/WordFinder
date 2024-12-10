import pytesseract
from PIL import Image, ImageEnhance
import numpy as np
import os
import pyautogui
from pynput import mouse
import re

# Specify the path to Tesseract executable
pytesseract.pytesseract.tesseract_cmd = '/opt/homebrew/bin/tesseract'

# Global variables to store mouse click coordinates
coords = []

# Listener to grab two mouse click coordinates
def on_click(x, y, button, pressed):
    if pressed:
        print(f"Mouse clicked at ({x}, {y})")
        coords.append((x, y))
        if len(coords) == 2:  # Stop after capturing two points
            return False

# Captures a screenshot of the board given the region
def capture_region():
    print("Capturing game board. Click the top left corner and the bottom right corner of the board.")
    with mouse.Listener(on_click=on_click) as listener:
        listener.join()
    
    if len(coords) < 2:
        raise ValueError("Insufficient coordinates captured.")
    
    # Ensure coordinates are ordered correctly
    top_left = coords[0]
    bottom_right = coords[1]
    
    # Calculate the region to capture
    x = int(round(top_left[0]))
    y = int(round(top_left[1]))
    width = int(round(bottom_right[0] - x))
    height = int(round(bottom_right[1] - y))
    
    print(f"Capturing region: Top-left ({x}, {y}), Width {width}, Height {height}")
    screenshot = pyautogui.screenshot(region=(x, y, width, height))
    return screenshot

def process_tile(image_tile, save_path, row, col):
    """
    Process an individual tile, crop the bottom 15%, save it, and extract the letter using Tesseract.
    """
    # Get tile dimensions
    width, height = image_tile.size

    # Crop the top and bottom 15% of the tile
    top_crop = int(height * 0.15)  # Top 15%
    bottom_crop = int(height * 0.85)  # Bottom 85%
    image_tile = image_tile.crop((0, top_crop, width, bottom_crop))

    # Convert the tile to grayscale
    image_gray = image_tile.convert('L')

    # Convert to numpy array for easier pixel manipulation
    image_array = np.array(image_gray)

    # Set a threshold for black pixels
    threshold = 50  # Adjust based on the image

    # Create a new white image
    image_processed = np.ones_like(image_array) * 255  # Start with a white image

    # Set pixels that are close to black (below threshold) to black
    image_processed[image_array < threshold] = 0

    # Convert back to an image
    image_result = Image.fromarray(image_processed)

    # Optionally, enhance contrast to make the black letters more pronounced
    enhancer = ImageEnhance.Contrast(image_result)
    image_result = enhancer.enhance(2)  # Adjust the contrast factor if necessary
    image_result = image_result.resize((100, 100), Image.Resampling.LANCZOS)

    # Save the processed tile
    # tile_filename = f"tile_{row + 1}_{col + 1}.png"
    # image_result.save(os.path.join(save_path, tile_filename))

    # Use pytesseract to extract the text (single letter)
    text = pytesseract.image_to_string(image_result, config='--psm 10 --oem 3').strip()
    text = re.sub(r'[^A-Z]', '', text)
    if len(text) > 1:
        text = text[0]
        
    # Fallback: Assume 'I' if no text detected
    if not text:
        text = input(f"Tile ({row + 1}, {col + 1}) did not return a letter. Input letter: ").strip()

    return text

def print_board(letters):
    print("\nDetected Letters:")
    for row in letters:
        print(" ".join(row))
    print()

def process_board(board_image, board_size, output_folder):
    """
    Process a board image (4x4 or 5x5), crop the bottom 15% of each tile, save the processed tiles,
    and print the detected letters for each tile.
    """
    # Get image dimensions
    width, height = board_image.size

    # Calculate tile size (assuming the board is evenly divided)
    tile_width = width // board_size
    tile_height = height // board_size

    # Create output folder if it doesn't exist
    # os.makedirs(output_folder, exist_ok=True)

    print(f"Processing a {board_size}x{board_size} board...")

    letters = []
    for row in range(board_size):
        letters_row = []
        for col in range(board_size):
            # Define the crop box for each tile
            left = col * tile_width
            upper = row * tile_height
            right = (col + 1) * tile_width
            lower = (row + 1) * tile_height

            # Crop the tile
            tile = board_image.crop((left, upper, right, lower))

            # Process the tile to extract the letter
            letter = process_tile(tile, output_folder, row, col)
            letters_row.append(letter)
        letters.append(letters_row)
    
    print_board(letters)
    while input("Edit any tiles? (y/n): ").lower() == 'y':
        row = int(input("Enter row (1-based): ")) - 1
        col = int(input("Enter column (1-based): ")) - 1
        new_letter = input("Enter the correct letter: ").strip()
        letters[row][col] = new_letter
        print_board(letters)
    return letters

# Main program
def main():
    try:
        # Specify the board size (e.g., 4 or 5)
        board_size = int(input("Enter the board size (e.g., 4 for 4x4): "))
        
        # Capture the region of the board
        board_image = capture_region()

        # Specify output folder for processed tiles
        output_folder = "output_tiles"

        # Process the board
        letters = process_board(board_image, board_size, output_folder)
        
        # Print the board
        print("\nFinal Board: ")
        print_board(letters)
        
        return board_size, letters
    
    except Exception as e:
        print(f"Error: {e}")
    
if __name__ == "__main__":
    main()
