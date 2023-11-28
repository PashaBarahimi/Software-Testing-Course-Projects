import unittest

from main import calculate_total_cost

INVALID_INPUT = "Invalid input"

A = [-1, 0, 1, 2]
B = [-1, 0, 1]
C = [-1, 0, 0.5, 1, 2]


class TestCalculateTotalCost(unittest.TestCase):
  def test_invalid_inputs_should_fail(self):
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[0], B[0], C[0]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[0], B[2], C[1]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[0], B[1], C[2]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[0], B[0], C[3]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[0], B[0], C[4]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[1], B[1], C[0]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[1], B[0], C[1]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[1], B[2], C[2]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[1], B[1], C[3]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[1], B[1], C[4]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[2], B[1], C[1]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[2], B[0], C[2]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[3], B[0], C[0]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[3], B[1], C[4]))

  def test_invalid_discount_should_fail(self):  # This case is not covered in the main.py
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[2], B[2], C[0]))
    self.assertEqual(INVALID_INPUT, calculate_total_cost(A[2], B[2], C[4]))

  def test_valid_inputs_should_pass(self):
    self.assertEqual(0, calculate_total_cost(A[2], B[2], C[3]))
    self.assertEqual(2, calculate_total_cost(A[3], B[2], C[1]))
    self.assertEqual(1, calculate_total_cost(A[3], B[2], C[2]))
    self.assertEqual(0, calculate_total_cost(A[3], B[2], C[3]))


if __name__ == '__main__':
  unittest.main()
