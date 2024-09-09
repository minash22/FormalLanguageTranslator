
public class MultiCommentDFA {
    public static boolean result(String s) {
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (ch == '/')
                        state = 1;
                    else if (ch == '*' || ch == 'a')
                        state = 0;
                    else
                        state = -1;
                    break;

                case 1:
                    if (ch == '*')
                        state = 2;
                    else if (ch == '/')
                        state = 1;
                    else if (ch == 'a')
                        state = 0;
                    else
                        state = -1;
                    break;
                case 2:
                    if (ch == '/' || ch == 'a')
                        state = 2;
                    else if (ch == '*')
                        state = 3;
                    else
                        state = -1;
                    break;

                case 3:
                    if (ch == '/')
                        state = 4;
                    else if (ch == 'a')
                        state = 2;
                    else if (ch == '*')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 4:
                    if (ch == 'a' || ch == '*')
                        state = 0;
                    else if (ch == '/')
                        state = 1;
                    else
                        state = -1;
                    break;

            }
        }
        return state == 4 || state == 0;
        // return state != 3 && state >= 0;
    }

    public static void main(String[] args) {
        String[] validExamples = { "aaa/****/aa", "aa/*a*a*/", "aaaa", "/****/", "/*aa*/", "*/a", "a/**/***a",
                "a/**/***/a", "a/**/aa/***/a" };
        String[] invalidExamples = { "aaa/*/aa", "a/**//***a", "aa/*aa" };

        System.out.println("Accepted examples:");
        for (String example : validExamples) {
            System.out.println(example + ": " + (result(example) ? "Accepted" : "Rejected"));
        }

        System.out.println("\nRejected examples:");
        for (String example : invalidExamples) {
            System.out.println(example + ": " + (result(example) ? "Accepted" : "Rejected"));
        }
    }
}