import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpStatusCode } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import { User } from '../model/user.model';
import { MessageService } from './message.service';

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private usersUrl: String = 'http://localhost:8080/users';
  private currentUser = new BehaviorSubject<User | null>(null);

  httpOptions = {
    headers: new HttpHeaders({ })
  };

  constructor(
    private http: HttpClient,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.log(`current user is ${this.currentUser.value}`);
    console.log(`current user is ${this.currentUser.value}`);
  }

  /** Log a UsersService message with the MessageService */
  private log(message: string) {
    this.messageService.add(`UsersService: ${message}`);
  }

  /** GET user by id. Will 404 if id not found */
  getUserById(userId: number): Observable<User> {
    const url = `${this.usersUrl}/${userId}`;
    return this.http.get<User>(url).pipe(
      tap(_ => this.log(`fetched user w/ userId=${userId}`)),
      catchError(this.handleError<User>(`getUser userId=${userId}`))
    );
  }

  /** POST: create the user on the server */
  registerUser(username: String): Observable<User | { success: boolean; message: string }> {
    return this.http.post<User>(`${this.usersUrl}/${username}`, this.httpOptions).pipe(
      tap((newUser: User) => this.log(`registering user w/ userId=${newUser.userId}, username=${newUser.username}`)),
      catchError((error) => {
        if (error.status === HttpStatusCode.Conflict) {
          this.log(`user w/ username=${username} already exists`);
          return of({success: false, message: 'User already exists'});
        }
        return this.handleError<User>('registerUser')(error);
      })
    );
  }

  /** POST: login the user on the server */
  loginUser(username: String): Observable<User | { success: boolean; message: string }> {
    return this.http.put<User>(`${this.usersUrl}/login/${username}`, this.httpOptions).pipe(
      tap((newUser: User) => this.log(`logging in user w/ userId=${newUser.userId} username=${newUser.username}`)),
      catchError((error) => {
        if (error.status === HttpStatusCode.NotFound) {
          this.log(`user w/ username=${username} does not exist`);
          return of({success: false, message: 'User does not exist'});
        }
        return this.handleError<User>('loginUser')(error);
      })
    );
  }

  /** POST: logout the user on the server */
  logoutUser(username: String): Observable<User | { success: boolean; message: string }> {
    return this.http.put<User>(`${this.usersUrl}/logout/${username}`, this.httpOptions).pipe(
      tap((newUser: User) => this.log(`logging out user w/ userId=${newUser.userId} username=${newUser.username}`)),
      catchError((error) => {
        if (error.status === HttpStatusCode.NotFound) {
          this.log(`user w/ username=${username} does not exist`);
          return of({success: false, message: 'User does not exist'});
        }
        return this.handleError<User>('logoutUser')(error);
      })
    );
  }

  /** Sets the current user, and saves to localStorage */
  setCurrentUser(user: User | null): void {
    this.log(`setting current user to ${JSON.stringify(user)}`);
    this.currentUser.next(user);
  }

  /** Returns the current user */
  getCurrentUser(): BehaviorSubject<User | null> {
    return this.currentUser;
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   *
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
  */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
