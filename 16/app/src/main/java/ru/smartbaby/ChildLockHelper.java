package ru.smartbaby;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public final class ChildLockHelper {
    private static final long HOLD_TIME_MS = 2000L;
    private static final Random RANDOM = new Random();
    private static String lastQuestion = "";

    private ChildLockHelper() {
    }

    public static void attachHoldToCorner(FrameLayout root, final Activity activity, final Runnable onVerified) {
        // Угол почти незаметен во время игры и показывает прогресс только при удержании.
        HoldCornerView holdCornerView = new HoldCornerView(activity);
        holdCornerView.setOnHoldCompleteListener(new HoldCornerView.OnHoldCompleteListener() {
            @Override
            public void onHoldComplete() {
                showParentChallenge(activity, onVerified);
            }
        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                Ui.dp(activity, 96),
                Ui.dp(activity, 96),
                Gravity.TOP | Gravity.END
        );
        params.topMargin = Ui.dp(activity, 6);
        params.rightMargin = Ui.dp(activity, 6);
        root.addView(holdCornerView, params);
    }

    public static void showParentChallenge(final Activity activity, final Runnable onVerified) {
        final ParentChallenge challenge = createParentChallenge();

        final EditText answerInput = new EditText(activity);
        answerInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        answerInput.setHint("Ответ");
        answerInput.setTextSize(20f);
        answerInput.setPadding(Ui.dp(activity, 16), Ui.dp(activity, 16), Ui.dp(activity, 16), Ui.dp(activity, 16));

        TextView questionView = new TextView(activity);
        questionView.setText(challenge.question);
        questionView.setTextColor(Color.rgb(45, 55, 88));
        questionView.setTextSize(24f);
        questionView.setPadding(0, 0, 0, Ui.dp(activity, 10));

        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(Ui.dp(activity, 12), Ui.dp(activity, 12), Ui.dp(activity, 12), Ui.dp(activity, 4));
        container.addView(questionView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        container.addView(answerInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Подтверждение для взрослого")
                .setView(container)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Проверить", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String answer = answerInput.getText().toString().trim();
                        if (String.valueOf(challenge.answer).equals(answer)) {
                            dialog.dismiss();
                            if (onVerified != null) {
                                onVerified.run();
                            }
                        } else {
                            Toast.makeText(activity, "Ответ неверный", Toast.LENGTH_SHORT).show();
                            answerInput.setText("");
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private static ParentChallenge createParentChallenge() {
        ParentChallenge challenge;
        do {
            if (RANDOM.nextBoolean()) {
                int first = 1 + RANDOM.nextInt(5);
                int second = 1 + RANDOM.nextInt(5);
                challenge = new ParentChallenge(
                        "Сколько будет " + first + " + " + second + "?",
                        first + second
                );
            } else {
                int second = 1 + RANDOM.nextInt(4);
                int answer = 1 + RANDOM.nextInt(5);
                int first = answer + second;
                challenge = new ParentChallenge(
                        "Сколько будет " + first + " - " + second + "?",
                        answer
                );
            }
        } while (challenge.question.equals(lastQuestion));

        lastQuestion = challenge.question;
        return challenge;
    }

    private static class HoldCornerView extends View {
        interface OnHoldCompleteListener {
            void onHoldComplete();
        }

        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF arcBounds = new RectF();
        private long startedAt;
        private boolean holding;
        private OnHoldCompleteListener listener;

        private final Runnable ticker = new Runnable() {
            @Override
            public void run() {
                if (!holding) {
                    return;
                }
                long elapsed = SystemClock.uptimeMillis() - startedAt;
                if (elapsed >= HOLD_TIME_MS) {
                    holding = false;
                    handler.removeCallbacks(this);
                    invalidate();
                    if (listener != null) {
                        listener.onHoldComplete();
                    }
                    return;
                }
                invalidate();
                handler.postDelayed(this, 16L);
            }
        };

        HoldCornerView(Activity activity) {
            super(activity);
            setClickable(true);
            arcPaint.setStyle(Paint.Style.STROKE);
            arcPaint.setStrokeWidth(Ui.dp(activity, 5));
            arcPaint.setStrokeCap(Paint.Cap.ROUND);
            arcPaint.setColor(Color.WHITE);
            dotPaint.setColor(Color.WHITE);
        }

        void setOnHoldCompleteListener(OnHoldCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!holding) {
                return;
            }

            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            float radius = Math.min(getWidth(), getHeight()) * 0.34f;

            fillPaint.setColor(Color.argb(110, 14, 24, 44));
            canvas.drawCircle(cx, cy, radius, fillPaint);
            canvas.drawCircle(cx - Ui.dp(getContext(), 8), cy, Ui.dp(getContext(), 3), dotPaint);
            canvas.drawCircle(cx, cy, Ui.dp(getContext(), 3), dotPaint);
            canvas.drawCircle(cx + Ui.dp(getContext(), 8), cy, Ui.dp(getContext(), 3), dotPaint);

            float progress = Math.min(1f, (SystemClock.uptimeMillis() - startedAt) / (float) HOLD_TIME_MS);
            arcBounds.set(cx - radius, cy - radius, cx + radius, cy + radius);
            canvas.drawArc(arcBounds, -90f, progress * 360f, false, arcPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    holding = true;
                    startedAt = SystemClock.uptimeMillis();
                    handler.removeCallbacks(ticker);
                    handler.post(ticker);
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getX() < 0 || event.getY() < 0 || event.getX() > getWidth() || event.getY() > getHeight()) {
                        cancelHold();
                    }
                    return true;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    cancelHold();
                    performClick();
                    return true;
                default:
                    return true;
            }
        }

        private void cancelHold() {
            holding = false;
            handler.removeCallbacks(ticker);
            invalidate();
        }

        @Override
        public boolean performClick() {
            super.performClick();
            return true;
        }
    }

    private static class ParentChallenge {
        final String question;
        final int answer;

        ParentChallenge(String question, int answer) {
            this.question = question;
            this.answer = answer;
        }
    }
}
