import React from 'react';
import { Row, Col, Icon, Navbar as MyNavbar } from 'react-materialize';
import { Link } from 'react-router-dom';
import './Navbar.scss';

export default function Navbar(props) {
    return (
        <MyNavbar>
            <Row className='cp-navbar'>
                <Col className='navbar-container' offset="s0 m1 l2" s={12} m={10} l={8}>
                    <Link to="/" className='NavItem cp-left-navitem left'>
                        <Icon small>home</Icon>
                        Home
                    </Link>
                    <Link to="/" className='NavItem cp-right-navitem right' onClick={props.handleLogout}>
                        <Icon small>power_settings_new</Icon>
                        Sign out
                    </Link>
                    <Link to="/profile" className='NavItem cp-right-navitem right'>
                        <Icon small>account_circle</Icon>
                        Account
                    </Link>
                    <Link to="/" className='NavItem cp-right-navitem-text right'>
                        {
                            'Hello, ' + props.userName + '!'
                        }
                    </Link>
                </Col>
            </Row>
        </MyNavbar>

    );
}