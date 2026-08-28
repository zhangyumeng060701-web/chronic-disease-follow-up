import { afterEach, describe, expect, it, vi } from 'vitest';
import { useDebounce } from '@/composables/useDebounce';

describe('useDebounce', () => {
  afterEach(() => {
    vi.useRealTimers();
  });

  it('only calls function after delay', () => {
    vi.useFakeTimers();
    const fn = vi.fn();
    const debounced = useDebounce(fn, 300);

    debounced();
    expect(fn).not.toHaveBeenCalled();

    vi.advanceTimersByTime(300);
    expect(fn).toHaveBeenCalledTimes(1);
  });

  it('cancels previous pending call', () => {
    vi.useFakeTimers();
    const fn = vi.fn();
    const debounced = useDebounce(fn, 300);

    debounced();
    debounced();
    vi.advanceTimersByTime(300);

    expect(fn).toHaveBeenCalledTimes(1);
  });
});
