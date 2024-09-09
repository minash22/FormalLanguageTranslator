public class FloatingPointDFA {

    public static boolean isFloatingPoint(String input) {
        int state = 0;

        for (char c : input.toCharArray()) {
            switch (state) {
                case 0:
                    if (Character.isDigit(c))
                        state = 1;
                    else if (c == '+' || c == '-')
                        state = 2;
                    else if (c == '.')
                        state = 8;
                    else
                        return false; // Invalid input

                    break;
                case 1:
                    if (Character.isDigit(c))
                        state = 1;
                    else if (c == '.')
                        state = 3;
                    else if (c == 'e')
                        state = 6;
                    else
                        return false; // Invalid input

                    break;
                case 2:
                    if (Character.isDigit(c))
                        state = 1;
                    else if (c == '.')
                        state = 8;
                    else
                        return false; // Invalid input

                    break;
                case 3:
                    if (Character.isDigit(c))
                        state = 4;
                    else
                        return false; // Invalid input

                    break;
                case 4:
                    if (Character.isDigit(c))
                        state = 4;
                    else if (c == 'e')
                        state = 6;
                    else
                        return false; // Invalid input

                    break;
                case 6:
                    if (Character.isDigit(c))
                        state = 7;
                    else if (c == '+' || c == '-')
                        state = 5;
                    else
                        return false; // Invalid input

                    break;
                case 5:
                    if (Character.isDigit(c))
                        state = 7;
                    else
                        return false; // Invalid input

                    break;
                case 7:
                    if (Character.isDigit(c))
                        state = 7;
                    else if (c == '.')
                        state = 8;
                    else
                        return false; // Invalid input

                    break;
                case 8:
                    if (Character.isDigit(c))
                        state = 4;
                    else
                        return false; // Invalid input

                    break;
                default:
                    return false; // Invalid state
            }
        }

        return state == 1 || state == 4 || state == 7;
    }

    public static void main(String[] args) {
        String[] validInputs = { "123", "123.5", ".567", "+7.5", "-.7", "67e10", "1e-2", "-.7e2", "1e2.3" };
        String[] invalidInputs = { ".", "e3", "123. ", "+e6", "1.2.3", "4e5e6", "++3" };

        System.out.println("Valid inputs:");
        for (String input : validInputs) {
            System.out.println(input + ": " + isFloatingPoint(input));
        }

        System.out.println("\nInvalid inputs:");
        for (String input : invalidInputs) {
            System.out.println(input + ": " + isFloatingPoint(input));
        }
    }
}
