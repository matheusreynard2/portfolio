import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private readonly PREFIX = 'prodify_';

  setItem(key: string, value: any): void {
    const serializedValue = JSON.stringify(value);
    localStorage.setItem(this.PREFIX + key, serializedValue);
  }

  getItem<T>(key: string, defaultValue: T): T {
    const item = localStorage.getItem(this.PREFIX + key);
    if (!item) return defaultValue;
    try {
      return JSON.parse(item) as T;
    } catch {
      return defaultValue;
    }
  }

  removeItem(key: string): void {
    localStorage.removeItem(this.PREFIX + key);
  }

  clear(): void {
    Object.keys(localStorage)
      .filter(key => key.startsWith(this.PREFIX))
      .forEach(key => localStorage.removeItem(key));
  }

  exists(key: string): boolean {
    return localStorage.getItem(this.PREFIX + key) !== null;
  }
} 